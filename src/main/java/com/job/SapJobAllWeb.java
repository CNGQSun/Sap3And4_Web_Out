package com.job;

import com.AppMainAll;
import com.controller.RunController;
import com.merck.dao.InsertMapper;
import com.merck.utils.DateUtils;
import com.sap.model.SqlStr;
import com.sap.utils.CSVUtil;
import com.sap.utils.ExcelReaderUtil;
import com.sap.utils.MyCombine;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 页面触发时将进入此类，调用run()方法
 */
@Component
public class SapJobAllWeb {

    private static Logger log = LoggerFactory.getLogger(SapJobAllWeb.class);
    private static final Properties p = RunController.p;
    public static Boolean flag = true;

    @Resource(name = "ImportDataImpl3")
    private com.merck.service.ImportData bean3;

    @Resource(name = "ImportDataImpl4")
    private com.sap.service.ImportData bean4;

    @Autowired
    private InsertMapper mapper;

    public void run() {
        flag = true;
        try {
            // 首先执行的是4.3,4.3执行结束执行4.4。如果4.3没有发现可执行文件，就直接执行4.4.
            log.info("SapJobAllJob 开始执行任务：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            // String sap3switch = p.getProperty("sap3.switch");
            String sap3switch = p.getProperty("sap3.ordeing");
            File file = new File(sap3switch);
            if (file.exists()) {
                log.info("Sap4.3开始执行" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                try {
                    excuteSap3();
                    // boolean flg = file.delete();
                    log.info("Sap4.3执行结束" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e) {
                    flag = false;
                    e.printStackTrace();
                    log.error("Sap4.3异常", e);
                }

            } else {
                log.info("Sap4.3没有发现可执行文件,没有发现   放单建议.xlsx" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            }
            excuteSap4();
            log.info("SapJobAllJob 执行任务结束：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            log.error("SapJobAllJob 执行任务异常：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), e);
        }
    }

    public void excuteSap3() throws Exception {
        try {
            // 执行bat脚本开始，本地测试时候可以将代码注释掉
            /*String batPath = p.getProperty("sap3.bat");

            File bathFile = new File(batPath);
            if (!bathFile.exists()) {
                log.info("Sap4.3没有发现bat脚本文件");
                return;
            }

            log.info("Sap4.3 开始执行bat文件" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            String cmd = "cmd /c start " + batPath;
            Process pro = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            pro.waitFor();
            log.info("Sap4.3执行bat文件结束" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            // 执行bat脚本结束*/
            String backOrderPath = p.getProperty("sap3.back.order");
            File backOrderFile = new File(backOrderPath);
            if (!backOrderFile.exists()) {
                log.info("Sap4.3没有发现Back_Order.txt文件");
                return;
            }
            String creditPath = p.getProperty("sap3.credit.limit");
            File creditFile = new File(creditPath);
            if (!creditFile.exists()) {
                log.info("Sap4.3没有发现credit_limit.txt文件");
                return;
            }
            mapper.saveOrUpdateData("truncate table BACK_ORDER_43;truncate table "
                    + " CREDIT_LIMIT;truncate table PY_SALES_MAPPING;truncate table SIGMA;truncate"
                    + " table ZCOP_AGING;truncate table SCORING;truncate table ORDERING;truncate table T_RELEASE_ORDER_EXCLUDE_PO;");

            List<String> title = new ArrayList<String>();
            title.add("Document Number");
            title.add("Partner");
            com.merck.utils.CSVUtil.readCsv(backOrderFile, "utf-8", title, bean3, "BACK_ORDER_43");

            //将错位数据列还原
            String sqlTemp = "select  * from BACK_ORDER_43  where CR_EXPOS like '%Y%' ";

            List<Map<String, Object>> selectData = mapper.selectData(sqlTemp);
            String sqlTemp2 = "";

            if (CollectionUtils.isNotEmpty(selectData)) {
                for (Map<String, Object> map : selectData) {
                    sqlTemp2 = "update BACK_ORDER_43 set TOTAL_AMT='" + map.get("PAYT") + "', PAYT='" + map.get("RISK_CLASS") + "', RISK_CLASS='" + map.get("CR_EXPOS") + "', CR_EXPOS='" + map.get("CREDIT_LIMIT") + "', CREDIT_LIMIT='" + map.get("DAYS_030") + "', \r\n" +
                            "DAYS_030='" + map.get("DAYS_3160") + "', DAYS_3160='" + map.get("DAYS_6190") + "', DAYS_6190='" + map.get("DAYS_91") + "', DAYS_91='" + map.get("UTILIZTN") + "', UTILIZTN='" + map.get("CHANGED_BY") + "', CHANGED_BY='" + map.get("LAST_CHANGED_ON") + "', LAST_CHANGED_ON=''  where ID='" + map.get("ID") + "' and DOCUMENT_NUMBER='" + map.get("DOCUMENT_NUMBER") + "' and PARTNER='" + map.get("PARTNER") + "' and CASE_ID='" + map.get("CASE_ID") + "' and CREATED_ON='" + map.get("CREATED_ON") + "'";
                    mapper.saveOrUpdateData(sqlTemp2);
                }
            }


            sqlTemp = "select  * from BACK_ORDER_43  where CR_EXPOS like '%N%' ";
            selectData = mapper.selectData(sqlTemp);
            if (CollectionUtils.isNotEmpty(selectData)) {
                for (Map<String, Object> map : selectData) {
                    sqlTemp2 = "update BACK_ORDER_43 set EXTERNAL_REFER='" + map.get("TOTAL_AMT") + "', TOTAL_AMT='" + map.get("RISK_CLASS") + "', PAYT='" + map.get("CR_EXPOS") + "', RISK_CLASS='" + map.get("CREDIT_LIMIT") + "', CR_EXPOS='" + map.get("DAYS_030") + "', CREDIT_LIMIT='" + map.get("DAYS_3160") + "', \r\n" +
                            "DAYS_030='" + map.get("DAYS_6190") + "', DAYS_3160='" + map.get("DAYS_91") + "', DAYS_6190='" + map.get("UTILIZTN") + "', DAYS_91='" + map.get("CHANGED_BY") + "', UTILIZTN='" + map.get("LAST_CHANGED_ON") + "', CHANGED_BY='', LAST_CHANGED_ON=''  where ID='" + map.get("ID") + "' and DOCUMENT_NUMBER='" + map.get("DOCUMENT_NUMBER") + "' and PARTNER='" + map.get("PARTNER") + "' and CASE_ID='" + map.get("CASE_ID") + "' and CREATED_ON='" + map.get("CREATED_ON") + "'";
                    mapper.saveOrUpdateData(sqlTemp2);
                }
            }


            mapper.saveOrUpdateData("update BACK_ORDER_43 set TOTAL_AMT='0' where TOTAL_AMT='';");


            //文件中包含标题
            List<String> titlecreateLimit = new ArrayList<String>();
            titlecreateLimit.add("Description");
            titlecreateLimit.add("Partner");
            com.merck.utils.CSVUtil.readCsv(creditFile, "utf-8", titlecreateLimit, bean3, "CREDIT_LIMIT");

            // 导入py_sales_mapping.xlsx
            String pySalesMappingPath = p.getProperty("sap3.py.sales.mapping");
            File pySalesMappingFile = new File(pySalesMappingPath);
            if (pySalesMappingFile.exists()) {
                com.merck.utils.ExcelReaderUtil.readExcel(bean3, pySalesMappingFile, "PY_SALES_MAPPING");
            } else {
                log.info("Sap4.3没有发现py_sales_mapping.xlsx文件");
            }

            // 导入Sigma
            String sigmaPath = p.getProperty("sap3.sigma");
            File sigma = new File(sigmaPath);
            if (sigma.exists()) {
                com.merck.utils.ExcelReaderUtil.readExcel(bean3, sigma, "SIGMA");
            } else {
                log.info("Sap4.3没有发现sigma.xlsx文件");
            }

            // 导入ZCOP Aging.xlsx

            String zcopPath = p.getProperty("sap3.zcop.aging");
            File zcopFile = new File(zcopPath);
            if (zcopFile.exists()) {
                com.merck.utils.ExcelReaderUtil.readExcel(bean3, zcopFile, "ZCOP_AGING");
            } else {
                log.info("Sap4.3没有发现zcop_aging.xlsx文件");
            }

            // 导入 打分情况.xlsx

            String scoringPath = p.getProperty("sap3.scoring");
            File scoringFile = new File(scoringPath);
            if (scoringFile.exists()) {
                com.merck.utils.ExcelReaderUtil.readExcel(bean3, scoringFile, "SCORING");
            } else {
                log.info("Sap4.3没有发现  打分情况.xlsx 文件");
            }

//			// 导入 打分情况.xlsx
//
            String orderingPath = p.getProperty("sap3.ordeing");
            File orderingFile = new File(orderingPath);
            if (orderingFile.exists()) {
                com.merck.utils.ExcelReaderUtil.readExcel(bean3, orderingFile, "ORDERING");
            } else {
                log.info("Sap4.3没有发现  欠款卡单.xlsx 文件");
            }

            //导入release order exclude po.xlsx 排除项
            String excludeOrderPath = p.getProperty("sap3.release.order.exclude.po");
            File excludeOrderPathFile = new File(excludeOrderPath);
            if (excludeOrderPathFile.exists()) {
                com.merck.utils.ExcelReaderUtil.readExcel(bean3, excludeOrderPathFile, "T_RELEASE_ORDER_EXCLUDE_PO");
            } else {
                log.info("Sap4.3没有发现 " + excludeOrderPath + " 文件");
            }

            String exportPath = p.getProperty("sap3.export.path");
            File exportFile = new File(exportPath);
            if (!exportFile.exists()) {
                exportFile.mkdirs();
            } else {
                File[] listFiles = exportFile.listFiles();
                for (int i = 0; i < listFiles.length; i++) {
                    listFiles[i].delete();
                }
            }
            Date nowDate = new Date();
            String target = DateUtils.format(nowDate, "MMdd");
            String date = DateUtils.format(nowDate, "yyyy-MM-dd") + " 12:00:00";
            String date2 = DateUtils.format(nowDate, "yyyy-MM-dd") + " 16:00:00";
            Date d = DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss");
            Date d2 = DateUtils.parse(date2, "yyyy-MM-dd HH:mm:ss");
            if (nowDate.getTime() < d.getTime()) {
                target = target + "A";
            } else {
                if (nowDate.getTime() < d2.getTime()) {
                    target = target + "P";
                } else {
                    target = target + "E";
                }
            }

//			数据修正sql
            String updateSql = p.getProperty("sap3.update.sql");
            if (StringUtils.isNotBlank(updateSql)) {
                mapper.saveOrUpdateData(updateSql);
            }
            // 导出数据 block order1226P
            Map<String, String> map = new LinkedHashMap<String, String>();
            map.put("无放单意见", com.merck.model.SqlStr.No_Bill_of_Lading_Opinion);
            map.put("无欠款卡单", com.merck.model.SqlStr.No_Debt_Card);
            // map.put("block order1226P", SqlStr.detail_Block);

            log.info("Sap4.3开始导出：无放单意见.xlsx、无欠款卡单.xlsx ，时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            bean3.exportDataExcle(map, exportPath + "4.3 result1 final result file" + target + ".xlsx", "excle",
                    getTitleMap(target), target);
            log.info("Sap4.3导出结束：无放单意见.xlsx、无欠款卡单.xlsx ，时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            Map<String, String> mapblock = new LinkedHashMap<String, String>();
            mapblock.put("block order" + target, com.merck.model.SqlStr.detail_Block);

            log.info("Sap4.3开始导出：block order" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            bean3.exportDataExcle(mapblock, exportPath + "4.3 result1 block order" + target + ".xlsx", "excle",
                    getTitleMap(target), target);
            log.info("Sap4.3导出结束：block order" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            Map<String, String> mapRelease = new LinkedHashMap<String, String>();
            mapRelease.put("Release order" + target, com.merck.model.SqlStr.detail_Release);

            log.info("Sap4.3开始导出：Release order" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            bean3.exportDataExcle(mapRelease, exportPath + "4.3 result1 Release order" + target + ".xlsx", "excle",
                    getTitleMap(target), target);
            log.info("Sap4.3导出结束：Release order" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            Map<String, String> map2 = new LinkedHashMap<String, String>();

            String saleHeadSql = "SELECT DISTINCT SALES_HEAD FROM SIGMA WHERE SALES_HEAD !='Steve Vermant & CFO' AND SALES_HEAD IS NOT NULL ORDER BY SALES_HEAD ";
            List<Map<String, Object>> saleHeadList = mapper.selectData(saleHeadSql);

            List<String> list = new ArrayList<String>();
            for (Map<String, Object> saleHead : saleHeadList) {
                list.add(saleHead.get("SALES_HEAD").toString());
            }

            if (!list.contains("George Ge")) {
                list.add("George Ge");
            }
            if (!list.contains("Steve Vermant & CFO")) {
                list.add("Steve Vermant & CFO");
            }
            Map<String, Map<String, String>> titleMap = getTitleMap(target);
            Map<String, String> detail4 = titleMap.get("Steve Vermant & CFO");
            titleMap.remove("Steve Vermant & CFO");
            titleMap.remove("George Ge");

            String str = "";
            String str2 = "";
            for (int i = 0; i < list.size() - 2; i++) {
                if ((list.get(i) != "Steve Vermant & CFO") && (list.get(i) != "George Ge")) {
                    titleMap.put(list.get(i), detail4);
                    map2.put(list.get(i), com.merck.model.SqlStr.getOrderRelaseSql(
                            new String[]{" = '" + list.get(i) + "'", "'" + "SALESHEAD&CREDITMANAGEMENTHEAD" + "'"}));
                    str = str + "'" + list.get(i) + "',";
                    if (list.get(i).equalsIgnoreCase("Wei Zhu")) {
                        str2 += "'" + list.get(i) + "',";
                    } else if (list.get(i).equalsIgnoreCase("Jason Song")) {
                        str2 += "'" + list.get(i) + "',";
                    } else if (list.get(i).equalsIgnoreCase("Channel")) {
                        str2 += "'" + list.get(i) + "',";
                    } else if (list.get(i).equalsIgnoreCase("Adam xu")) {
                        str2 += "'" + list.get(i) + "',";
                    }
                }
            }
            str2 += "'George Ge'";
            if (str.endsWith(",")) {
                str = str.substring(0, str.lastIndexOf(","));
            }
            titleMap.put(list.get(list.size() - 2), detail4);
            titleMap.put(list.get(list.size() - 1), detail4);
            map2.put(list.get(list.size() - 2), com.merck.model.SqlStr.getOrderRelaseSql(
                    new String[]{" in ( " + str2 + ")", "'" + "GEORGEGE" + "'"}) + " order by head");
            map2.put(list.get(list.size() - 1), com.merck.model.SqlStr.getOrderRelaseSql(
                    new String[]{" in ( " + str + ")", "'" + "STEVEVERMANT&CFO" + "'"}) + " order by head");
            log.info("Sap4.3开始导出：order release application" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            bean3.exportDataExcle(map2, exportPath + "4.3 result2 order release application" + target + ".xlsx",
                    "excle", titleMap, target);
            log.info("Sap4.3导出结束：order release application" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            String backUpPath = p.getProperty("sap3.backup.path");
            File backUpFile = new File(backUpPath);
            if (!backUpFile.exists()) {
                backUpFile.mkdirs();
            }

            String dateStr = DateUtils.format(new Date(), "yyyyMMddHHmmssSSS");
            String backUpbackOrderfileName = backOrderFile.getName().replace(".txt", dateStr + ".txt");
            File backUpbackOrder = new File(backUpPath + backUpbackOrderfileName);
            boolean renameTo = backOrderFile.renameTo(backUpbackOrder);
            log.info("Sap4.3 备份back_order.txt 到" + backUpbackOrder.getAbsolutePath() + (renameTo ? "成功" : "失败"));

            String backUpCreditLimitfileName = creditFile.getName().replace(".txt", dateStr + ".txt");
            File backUpcreditFile = new File(backUpPath + backUpCreditLimitfileName);
            boolean renameTo2 = creditFile.renameTo(backUpcreditFile);
            log.info("Sap4.3 备份credit_limit.txt 到" + backUpcreditFile.getAbsolutePath() + (renameTo2 ? "成功" : "失败"));

            //备份 放单建议.xlsx
            boolean ordering = false;
            String name = orderingFile.getName();
            String string = backUpPath + name.replace(".xlsx", dateStr + ".xlsx");
            File filecopy = new File(string);
            ordering = FileCopyUtils.copy(orderingFile, filecopy) > 0;
            log.info("Sap4.3 备份放单建议.xlsx到" + filecopy.getAbsolutePath() + (ordering ? " 成功！ " : " 失败！ "));

        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            throw e;
        }

    }

    public void excuteSap4() {

        log.info("Sap4.4已经开始执行任务：开始时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

        while (true) {
            if (p.get("blank.path") != null) {
                break;
            }
            System.out.println("wait laod properties");
        }

        try {
            File blankOrderFile = null;
            String blankOrderPath = "";
            while (true) {
                blankOrderPath = p.getProperty("blank.path");
                blankOrderFile = new File(blankOrderPath);
                if (blankOrderFile.exists()) {
                    break;
                } else {
                    log.info("Sap4.4没有发现blank.xlsx文件");
                    log.info("Sap4.4执行任务结束：结束时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    return;
                }
            }

/*            // 触发python脚本，等待执行
            log.info("Sap4.4开始执行bat文件" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            String batPath = p.getProperty("bat.path");
            String cmd = "cmd /c start " + batPath;
            Process pro = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            pro.waitFor();
            log.info("Sap4.4执行bat文件结束" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            // 触发python脚本，执行结束*/
            // 获取sap文件

            String backOrderPath = p.getProperty("sap.path");
            File backOrderFile = new File(backOrderPath);

            if (!backOrderFile.exists()) {
                log.info("Sap4.4没有发现Back_Order.txt文件");
                log.info("Sap4.4执行任务结束：结束时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                return;
            }

            String mappingOrderPath = p.getProperty("mapping.path");
            File mappingFile = new File(mappingOrderPath);
            if (!mappingFile.exists()) {
                log.info("Sap4.4没有发现mapping.xlsx文件");
            }

            String exportPath = p.getProperty("export.path");
            final List<String> title = new ArrayList<String>();
            title.add("Document Number");
            title.add("Partner");
            // 清空表
            bean4.truncateTableData(SqlStr.truncate_sql);
            CSVUtil.readCsv(backOrderFile, "utf-8", title, bean4, "BACK_ORDER");
            ExcelReaderUtil.readExcel(bean4, blankOrderFile, "BLANK_ORDER");
            if (mappingFile.exists())
                ExcelReaderUtil.readExcel(bean4, mappingFile, "SAP_MAPPING");
            // 处理mapping
            bean4.truncateTableData(SqlStr.hander_mapping_sql);
            // 导出Excle数据
            bean4.truncateTableData(SqlStr.export_data3);

            // 将数据导入到temp2
            bean4.truncateTableData(SqlStr.create_back_order_temp2);

            // 获取存在问题的数据
            List<Map<String, Object>> selectData = bean4.selectData(SqlStr.get_blank_order);

            if (CollectionUtils.isNotEmpty(selectData)) {
                MyCombine tp = new MyCombine();
                for (Map<String, Object> map : selectData) {
                    String loan = (String) map.get("LOAN");
                    String descrip = (String) map.get("RE_PAY_NAME");
                    String payer = (String) map.get("PAYER");

                    String sql2 = "select  * from  BACK_ORDER a  where a.description='" + descrip
                            + "' and  a.payt='PPD' and risk_class='YS1'";

                    List<Map<String, Object>> selectData2 = bean4.selectData(sql2);
                    if (CollectionUtils.isNotEmpty(selectData)) {
                        int size = selectData2.size();
                        if (size <= 2) {
                            continue;
                        }
                        String[] index = new String[size];
                        for (int i = 0; i < size; i++) {
                            index[i] = i + "";
                        }
                        flg:
                        for (int j = 2; j < size; j++) {

                            List<String> combine = tp.combine(index, j);
                            for (String strindex : combine) {
                                if (StringUtils.isNoneBlank(strindex)) {
                                    String[] split = strindex.split(" +");
                                    String pos = "";
                                    double s = 0;
                                    for (int i = 0; i < split.length; i++) {

                                        Map<String, Object> mapData = selectData2.get(Integer.valueOf(split[i]));
                                        String po = (String) mapData.get("DOCUMENT_NUMBER");
                                        String object = (String) mapData.get("TOTAL_AMT");
                                        if (StringUtils.isNoneBlank(object)) {
                                            double totalAmt = Double.valueOf(object);
                                            s = s + totalAmt;
                                        }

                                        pos = pos + " " + po;
                                    }
                                    if (s == Double.valueOf(loan)) {
                                        if (pos.startsWith(" ")) {
                                            pos = pos.substring(pos.indexOf(" ") + 1);
                                        }
                                        String sql3 = "update BLANK_ORDER_TEMP2 set po='" + pos + "' where LOAN='"
                                                + loan + "'  and RE_PAY_NAME='" + descrip + "'  and payer='" + payer
                                                + "'";
                                        bean4.truncateTableData(sql3);

                                        break flg;
                                    }
                                }

                            }

                        }

                    }

                }

            }

            String sqlFinal = "select * from BLANK_ORDER_TEMP2 order by id+0";

            bean4.exportDataExcle(sqlFinal, exportPath, "excle");
            bean4.truncateTableData(SqlStr.drop_sql);
            String suf = DateUtils.format(new Date(), "yyyyMMddHHmmssSSS");
            boolean flgBank = false;
            boolean flgBack = false;
            boolean flgMapping = false;
            String BackUpPath = p.getProperty("backUp.path");
            File BackUpFile = new File(BackUpPath);
            if (!BackUpFile.exists()) {
                BackUpFile.mkdirs();
            }

/*            if (blankOrderFile != null && blankOrderFile.exists()) {
                String name = blankOrderFile.getName();
                String string = BackUpPath + name.replace(".xlsx", suf + ".xlsx");
                File fileCopy = new File(string);
                flgBank = blankOrderFile.renameTo(fileCopy);
                log.info("Sap4.4备份移动bank.xlsx到" + fileCopy.getAbsolutePath() + (flgBank ? " 成功！ " : " 失败！ "));
            }*/
            //备份blank.xlsx
            if (blankOrderFile != null && blankOrderFile.exists()) {
                String name = blankOrderFile.getName();
                String string = BackUpPath + name.replace(".xlsx", suf + ".xlsx");
                File filecopy = new File(string);
                flgBank = FileCopyUtils.copy(blankOrderFile, filecopy) > 0;
                log.info("Sap4.4 备份blank.xlsx到" + filecopy.getAbsolutePath() + (flgBank ? " 成功！ " : " 失败！ "));
            }

            if (backOrderFile != null && backOrderFile.exists()) {
                String name = backOrderFile.getName();
                String string = BackUpPath + name.replace(".txt", suf + ".txt");
                File fileCopy = new File(string);
                // flgBack = FileCopyUtils.copy(backOrderFile, fileCopy) > 0;
                flgBack = backOrderFile.renameTo(fileCopy);
                log.info("Sap4.4备份back_order.txt到" + fileCopy.getAbsolutePath() + (flgBack ? " 成功！ " : " 失败！ "));
            }
            if (mappingFile != null && mappingFile.exists()) {
                String name = mappingFile.getName();
                String string = BackUpPath + name.replace(".xlsx", suf + ".xlsx");
                File filecopy = new File(string);
                flgMapping = FileCopyUtils.copy(mappingFile, filecopy) > 0;
                log.info("Sap4.4备份mapping.xlsx到" + filecopy.getAbsolutePath() + (flgMapping ? " 成功！ " : " 失败！ "));
            }
            log.info("Sap4.4执行任务结束：结束时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            log.error("Sap4.4执行出异常：时间" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), e);
        }

    }

    public static Map<String, Map<String, String>> getTitleMap(String target) {
        Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
        Map<String, String> detail = new LinkedHashMap<String, String>();
        detail.put("CC", "CC");
        detail.put("DOCUMENT_NUMBER", "Document");
        detail.put("PARTNER", "Cred. acct");
        detail.put("DESCRIPTION", "Name 1");
        detail.put("EXTERNAL_REFER", "External refer.");
        detail.put("CREDIT_VALUE", "Credit value");
        detail.put("RECEIVABLES", " Receivables");
        detail.put("OPEN_ORDER", "Open order");
        detail.put("CREDIT_LIMIT", "Credit limit");
        detail.put("RISK", "Risk");
        detail.put("DAYS_30_M", "30+<100");
        detail.put("R_OR_B", "放单建议（block or release）");
        detail.put("EXPOSURE", "exposure");
        detail.put("OVERRUNS", "Overruns=exposure- credit limit");
        detail.put("DEVIATION_PERCENT", "Deviation %= Overruns / Credit limit * 100%");
        detail.put("ORDER_RELEASE_MATRIX", "Order release matrix");
        detail.put("SALES", "sales");
        detail.put("HEAD", "head");
        detail.put("BUINESS_HEAD", "Buiness head");
        map.put("block order" + target, detail);
        map.put("Release order" + target, detail);
        Map<String, String> detail2 = new LinkedHashMap<String, String>();
        detail2.put("CC", "CC");
        detail2.put("DOCUMENT_NUMBER", "Document");
        detail2.put("PARTNER", "Cred. acct");
        detail2.put("NAME1", "Name 1");
        map.put("无放单意见", detail2);
        Map<String, String> detail3 = new LinkedHashMap<String, String>();
        detail3.put("CC", "CC");
        detail3.put("DOCUMENT_NUMBER", "Document");
        detail3.put("PARTNER", "Cred. acct");
        detail3.put("NAME1", "Name 1");
        map.put("无欠款卡单", detail3);


        Map<String, String> detail4 = new LinkedHashMap<String, String>();
        detail4.put("PARTNER", "Payer");
        detail4.put("DESCRIPTION", "Names");
        detail4.put("HEAD", "Sales Head");
        detail4.put("RATING", "Rating");
        detail4.put("CREDIT_VALUE", "Order Value");
        detail4.put("RECEIVABLES", " Receivables");
        detail4.put("DAYS_30_M", "Overdue 30+");
        detail4.put("OPEN_ORDER", "Open order");
        detail4.put("CREDIT_LIMIT", "Credit limit");
        detail4.put("EXPOSURE", "Exposure");
        detail4.put("OVERRUNS", "Overruns");
        detail4.put("DEVIATION_PERCENT", "Deviation");

        //map.put("Adam xu", detail4);
        //map.put("Wei Zhu", detail4);
        //map.put("Jason Song", detail4);
        //map.put("Channel", detail4);
        //map.put("Mingfei Guo", detail4);
        //map.put("Seaman Wu", detail4);
        map.put("George Ge", detail4);
        map.put("Steve Vermant & CFO", detail4);
        return map;

    }


    public static void main(String[] args) {

        Map map = new LinkedHashMap();

        map.put("999", "2dddff");
        map.put("888", "bbb");
        map.put("777", "aaa");

        map.put("999", "ccc");

        System.out.println(map);


    }
}
