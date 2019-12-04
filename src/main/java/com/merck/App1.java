package com.merck;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.merck.dao.InsertMapper;
import com.merck.service.ImportData;
import com.merck.utils.DateUtils;

@SpringBootApplication
@ComponentScan("com.merck")
@MapperScan("com.merck.dao")
public class App1 {

    private static Logger log = LoggerFactory.getLogger(App1.class);

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext run = SpringApplication.run(App1.class, args);
            ImportData bean = run.getBean(ImportData.class);
            InsertMapper mapper = run.getBean(InsertMapper.class);
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
            //String exportPath=args[0];
            String exportPath = "d:\\zsw123467\\";
		/*	List<String> title = new ArrayList<String>();
			title.add("Document Number");
			title.add("Partner");
			com.merck.utils.CSVUtil.readCsv(new File("C:\\Users\\AAS\\Downloads\\Back_Order20190521151610820.txt"), "utf-8", title, bean, "BACK_ORDER_43");
*/
            //	mapper.saveOrUpdateData("update BACK_ORDER_43 set TOTAL_AMT='0' where TOTAL_AMT='';");


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


            mapper.saveOrUpdateData("update BACK_ORDER_43 set TOTAL_AMT='0' where TOTAL_AMT='';");


            Map<String, String> map = new LinkedHashMap<String, String>();
            map.put("无放单意见", com.merck.model.SqlStr.No_Bill_of_Lading_Opinion);
            map.put("无欠款卡单", com.merck.model.SqlStr.No_Debt_Card);
            //map.put("block order1226P", SqlStr.detail_Block);

            log.info("Sap4.3开始导出：无放单意见.xlsx、无欠款卡单.xlsx ，时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            bean.exportDataExcle(map, exportPath + "4.3 result1 final result file" + target + ".xlsx", "excle", getTitleMap(target), target);
            log.info("Sap4.3导出结束：无放单意见.xlsx、无欠款卡单.xlsx ，时间：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            Map<String, String> mapblock = new LinkedHashMap<String, String>();
            mapblock.put("block order" + target, com.merck.model.SqlStr.detail_Block);

            log.info("Sap4.3开始导出：block order" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            bean.exportDataExcle(mapblock, exportPath + "4.3 result1 block order" + target + ".xlsx", "excle",
                    getTitleMap(target), target);
            log.info("Sap4.3导出结束：block order" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));


            Map<String, String> mapRelease = new LinkedHashMap<String, String>();
            mapRelease.put("Release order" + target, com.merck.model.SqlStr.detail_Release);

            log.info("Sap4.3开始导出：Release order" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            bean.exportDataExcle(mapRelease, exportPath + "4.3 result1 Release order" + target + ".xlsx", "excle",
                    getTitleMap(target), target);
            log.info("Sap4.3导出结束：Release order" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            Map<String, String> map2 = new LinkedHashMap<String, String>();
            List<String> list = new ArrayList<String>();
            list.add("Wei Zhu");
            list.add("Jason Song");
            //Jun Li改为Channel
            list.add("Channel");
            list.add("Mingfei Guo");
            list.add("Seaman Wu");
            //新增Adam xu和George Ge
            list.add("Adam xu");
            list.add("Steve Vermant");
            list.add("George Ge");
            list.add("Steve Vermant & CFO");

            String str = "";
            String str2 = "";
            for (int i = 0; i < list.size() - 2; i++) {
                map2.put(list.get(i), com.merck.model.SqlStr.getOrderRelaseSql(
                        new String[]{" = '" + list.get(i) + "'", "'" + "SALESHEAD&CREDITMANAGEMENTHEAD" + "'"}));
                str = str + "'" + list.get(i) + "',";
                if (list.get(i).equalsIgnoreCase("Wei Zhu")){
                    str2 += "'" + list.get(i) + "',";
                }
                if (list.get(i).equalsIgnoreCase("Jason Song")){
                    str2 += "'" + list.get(i) + "',";
                }
                if (list.get(i).equalsIgnoreCase("Channel")){
                    str2 += "'" + list.get(i) + "',";
                }
                if (list.get(i).equalsIgnoreCase("Adam xu")){
                    str2 += "'" + list.get(i) + "',";
                }
            }
            str2+="'George Ge'";
            if (str.endsWith(",")) {
                str = str.substring(0, str.lastIndexOf(","));
            }
            map2.put(list.get(list.size() - 2), com.merck.model.SqlStr.getOrderRelaseSql(
                    new String[]{" in ( " + str2 + ")","'" + "GEORGEGE" + "'"}) + " order by head");

            map2.put(list.get(list.size() - 1), com.merck.model.SqlStr.getOrderRelaseSql(
                    new String[]{" in ( " + str + ")","'" + "STEVEVERMANT&CFO" + "'"}) + " order by head");

            log.info("Sap4.3开始导出：order release application" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            bean.exportDataExcle(map2, exportPath + "4.3 result2 order release application" + target + ".xlsx", "excle",
                    getTitleMap(target), target);
            log.info("Sap4.3导出结束：order release application" + target + ".xlsx ，时间："
                    + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            log.info("springBoot已经启动");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("异常：", e);
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

        //新增Adam xu和George Ge
        map.put("Adam xu", detail4);
        map.put("Wei Zhu", detail4);
        map.put("Jason Song", detail4);
        //Jun Li改为Channel
        map.put("Channel", detail4);
        map.put("Mingfei Guo", detail4);
        map.put("Seaman Wu", detail4);
        map.put("Steve Vermant", detail4);
        map.put("George Ge", detail4);
        map.put("Steve Vermant & CFO", detail4);
        return map;

    }

}
