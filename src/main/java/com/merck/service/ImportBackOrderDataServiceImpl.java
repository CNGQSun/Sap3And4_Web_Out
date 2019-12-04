package com.merck.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.merck.dao.InsertMapper;
import com.merck.utils.DateUtils;
import com.merck.utils.ExportUtil;

@Service("ImportDataImpl3")
public class ImportBackOrderDataServiceImpl implements ImportData {
    private static Logger log = LoggerFactory.getLogger(ImportBackOrderDataServiceImpl.class);

    @Autowired
    private InsertMapper mapper;

    @Override
    public void insertData(List<List<String>> list, Map<Integer, String> titleCsv, String type, String tableName,
                           List<String> indexList) {

        String val = "";
        List<Integer> integerList = new ArrayList<Integer>();
        for (Map.Entry<Integer, String> entry : titleCsv.entrySet()) {
            String value = entry.getValue().trim();
            Integer key = entry.getKey();
            val = val + value + ",";
            integerList.add(key);
        }
        if (val.endsWith(",")) {
            val = val.substring(0, val.lastIndexOf(","));
        }

        if ("csv".equalsIgnoreCase(type)) {

            if ("BACK_ORDER_43".equalsIgnoreCase(tableName)) {
                val = val.replace(".", "").replaceAll(" +", "_").replace("0-30_Days", "DAYS_030")
                        .replace("31-60_Days", "DAYS_3160").replace("61-90_Days", "DAYS_6190")
                        .replace("=>_91_Days", "DAYS_91").replace("Utiliztn_%", "Utiliztn")
                        .replace("Util(&)", "Utiliztn").replace("Limit", "CREDIT_LIMIT")
                        .replace("Credit_CREDIT_LIMIT", "CREDIT_LIMIT").replace("Ext_ref", "EXTERNAL_REFER");

                val = "ID," + val;
            } else if ("CREDIT_LIMIT".equalsIgnoreCase(tableName)) {
                val = val.replace(".", "").replaceAll(" +", "_").replace("Utilization_%", "Utilization")
                        .replace("Utiliztn_%", "Utiliztn").replace("Util(&)", "Utiliztn");
            }

        } else {

            val = val.replace("（", "(").replace("）", ")").replaceAll("(\\r\\n|\\r|\\n|\\n\\r)+", "");

            if ("PY_SALES_MAPPING".equalsIgnoreCase(tableName)) {
                val = val.replace("-", "_").replaceAll(" +", "_").replace("Name_1", "Name1").replace("Name", "Name1")
                        .replace("Name11", "Name1");
            }
            if ("SIGMA".equalsIgnoreCase(tableName)) {
                val = val.replaceAll(" +", "_").replace("信控", "CREDIT_CONTROL");
            }
            if ("ZCOP_AGING".equalsIgnoreCase(tableName)) {
                val = val.replace(".", "").replaceAll(" +", "_").replace("30+_Days", "Days_30_M")
                        .replace("0_To_030", "TO030").replace("031_To_060", "To3160").replace("061_To_090", "To6190")
                        .replace("031_To_060", "To3160").replace("061_To_090", "To6190")
                        .replace("091_To_120", "To91120").replace("121_+", "M121").replace("181_&_above", "M181")
                        .replace("R&B", "RB").replace("121_To_180", "TO121180").replace("Name_1", "Name");
            }
            if ("SCORING".equalsIgnoreCase(tableName)) {
                val = val.replace("-", "_").replaceAll(" +", "_");
            }
            if ("ORDERING".equalsIgnoreCase(tableName)) {
                val = val.replace("-", "_").replaceAll(" +", "_").replace("+", "").replace("1___30_Days", "Days130")
                        .replace("31___60_Days", "Days3160").replace("61___90_Days", "Days6190")
                        .replace("91___120_Days", "Days91120").replace("121___150_Days", "Days121150")
                        .replace("150___180_Days", "Days150180").replace("181___360_Days", "Days181360")
                        .replace("361___720_Days", "Days361720").replace(">_720_Days", "Days720")
                        .replace("0_Days", "Days0").replaceAll("Credit", "CREDIT_LIMIT").replace("Name_1", "Name");

            }

        }
        String values = "";

        for (int i = 0; i < list.size(); i++) {
            List<String> listVal = list.get(i);

            String tempStr = "(";
            // 解决插入和导出的顺序的问题
            if ("BACK_ORDER_43".equalsIgnoreCase(tableName) && !CollectionUtils.isEmpty(indexList)) {
                tempStr = tempStr + "'" + indexList.get(i) + "',";
            }
// 12,11,10,34     //10
            for (Integer integer2 : integerList) {

                if (integer2 > listVal.size() - 1) {
                    tempStr = tempStr + "'" + "" + "'" + ",";
                } else {

                    String value = listVal.get(integer2).trim().replace("（", "(").replace("）", ")")
                            .replaceAll("(\\r\\n|\\r|\\n|\\n\\r)+", "");
                    if (value.contains("'")) {
                        value = value.replace("'", "''");
                    }

                    String string = titleCsv.get(integer2).replaceAll(" +", "").replaceAll("(\\r\\n|\\r|\\n|\\n\\r)+",
                            "");
                    /*
                     * if (string.equalsIgnoreCase("description")) { String[] split =
                     * value.split(" +"); if (split.length >= 2) { value = split[1]; } }
                     */

                    if ("csv".equalsIgnoreCase(type) && (string.equalsIgnoreCase("TotalAmt")
                            || string.equalsIgnoreCase("贷") || string.equalsIgnoreCase("余额")
                            || string.equalsIgnoreCase("ExposureAmount") || string.equalsIgnoreCase("Cr.Expos.")
                            || string.equalsIgnoreCase("CreditLimit") || string.equalsIgnoreCase("Utilization%"))) {

                        int index1 = value.indexOf(".");
                        int index2 = value.indexOf(",");
                        // 23.435,45
                        if (index1 > 0 && index2 > 0 && index1 < index2) {
                            value = value.replace(".", "").replace(",", ".");
                        }

                        if (index1 > 0 && index2 > 0 && index1 > index2) {
                            value = value.replace(",", "");
                        }

                        /*
                         * if (value.indexOf(",") >= 0) { value = value.replace(",", ""); }
                         */
                    }

                    tempStr = tempStr + "'" + value.trim() + "'" + ",";
                }
            }

            if (tempStr.endsWith(",")) {
                tempStr = tempStr.substring(0, tempStr.lastIndexOf(","));
            }

            tempStr = tempStr + ")";

            values = values + tempStr + ",";

        }

        if (values.endsWith(",")) {
            values = values.substring(0, values.lastIndexOf(","));
        }

        String sql = "insert into " + tableName + "(" + val + ")values " + values;

        mapper.saveOrUpdateData(sql);

    }

    //public void truncateTableData(String sql) {
    //    try {
    //        mapper.saveOrUpdateData(sql);
    //    } catch (Exception e) {
    //        // TODO: handle exception
    //        e.printStackTrace();
    //        throw e;
    //    }
    //
    //}

    public void truncateTableData(String sql) {
        try {
            mapper.saveOrUpdateData(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void exportDataExcle(Map<String, String> map, String path, String type,
                                Map<String, Map<String, String>> mapTitles, String target) {
		/*	Date nowDate = new Date();
		String target = DateUtils.format(nowDate, "MMdd");
		String date = DateUtils.format(nowDate, "yyyy-MM-dd") + " 12:00:00";
		Date d = DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss");
		if (nowDate.getTime() < d.getTime()) {
			target = target + "A";
		} else {
			target = target + "P";
		}*/
        try {
            if ("excle".equalsIgnoreCase(type)) {
                Workbook wb = null;
                CellStyle cellStyle1 = null;
                CellStyle cellStyle2 = null;
                CellStyle cellStyleAppli = null;
                DataFormat createDataFormatAppli = null;
                DataFormat createDataFormat = null;

                CellStyle cellStyle3 = null;
                DataFormat createDataFormat1 = null;

                CellStyle cellStyle4 = null;
                DataFormat createDataFormat4 = null;

                XSSFCellStyle headStyle = null;
                XSSFCellStyle headStyleAppli = null;
                XSSFCellStyle bodyStyle = null;
                XSSFCellStyle bodyStyleAppli = null;

                XSSFCellStyle bodyStyle2 = null;
                XSSFCellStyle bodyStyleAppli2 = null;

                XSSFCellStyle cellStyle = null;

                Sheet sheet = null; // 工作表对象
                Row nRow = null; // 行对象
                Cell nCell = null; // 列对象
                FileOutputStream fOut = null;
                // 总行号
                int pageRowNo = 0; // 页行号
                boolean flg = true;
                File exportFile = new File(path);
                File parent = exportFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                if (!exportFile.exists()) {
                    exportFile.createNewFile();
                }
                // Map<String, String> maptitle = getMapperMap();
                List<Map<String, Object>> selectData = null;

                for (Map.Entry<String, String> entrys : map.entrySet()) {
                    int rowNo = 0;
                    String sql = entrys.getValue();
                    String sheetName = entrys.getKey();
                    Map<String, String> maptitle = mapTitles.get(sheetName);
                    if (StringUtils.isNoneBlank(sql)) {
                        selectData = mapper.selectData(sql);

                    }
                    if (sheetName.equalsIgnoreCase("Wei Zhu")) {
                        sheetName = "Pharma";
                    } else if (sheetName.equalsIgnoreCase("Adam xu")) {
                        sheetName = "Digital";
                    } else if (sheetName.equalsIgnoreCase("Channel")) {
                        sheetName = "Channel";
                    } else if (sheetName.equalsIgnoreCase("Jason Song")) {
                        sheetName = "Academia";
                    } else if (sheetName.equalsIgnoreCase("Mingfei Guo")) {
                        sheetName = "Diagnostic";
                    } else if (sheetName.equalsIgnoreCase("Seaman Wu")) {
                        sheetName = "AA I&T";
                    } else if (sheetName.equalsIgnoreCase("George Ge")) {
                        sheetName = "Business B";
                    } else if (sheetName.equalsIgnoreCase("Steve Vermant")) {
                        sheetName = "Business A";
                    }

                    if (sheetName.equalsIgnoreCase("Business B")) {
                        System.out.println("------------");
                        System.out.println(sql);
                        System.out.println("------------");
                    }

                    if (wb == null) {
                        wb = new SXSSFWorkbook(100);
                        cellStyle1 = wb.createCellStyle();
                        cellStyle1.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                        cellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        ExportUtil exportUtil = new ExportUtil(wb, sheet);
                        headStyle = (XSSFCellStyle) exportUtil.getHeadStyle();
                        headStyleAppli = (XSSFCellStyle) exportUtil.getHeadStyleForAppli();

                        bodyStyle = (XSSFCellStyle) exportUtil.getBodyStyleBord();
                        bodyStyleAppli = (XSSFCellStyle) exportUtil.getBodyStyleAppli();

                        bodyStyle2 = (XSSFCellStyle) exportUtil.getBodyStyleAndLeft();
                        bodyStyleAppli2 = (XSSFCellStyle) exportUtil.getBodyStyleLeftAppli();

                        Font font = wb.createFont();
                        font.setFontName("微软雅黑");
                        font.setFontHeight((short) 200);

                        Font font2 = wb.createFont();
                        font2.setFontName("微软雅黑");
                        font2.setFontHeight((short) 220);

                        cellStyle2 = wb.createCellStyle();
                        createDataFormat = wb.createDataFormat();
                        cellStyle2.setDataFormat(createDataFormat.getFormat("0%"));
                        cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
                        cellStyle2.setAlignment(HorizontalAlignment.LEFT);
                        cellStyle2.setFont(font);
                        cellStyle2.setBorderLeft(BorderStyle.THIN);
                        cellStyle2.setBorderBottom(BorderStyle.THIN);
                        cellStyle2.setBorderRight(BorderStyle.THIN);
                        cellStyle2.setBorderTop(BorderStyle.THIN);

                        cellStyleAppli = wb.createCellStyle();
                        createDataFormatAppli = wb.createDataFormat();
                        cellStyleAppli.setDataFormat(createDataFormatAppli.getFormat("0%"));
                        cellStyleAppli.setVerticalAlignment(VerticalAlignment.CENTER);
                        cellStyleAppli.setAlignment(HorizontalAlignment.CENTER);
                        cellStyleAppli.setFont(font2);
                        cellStyleAppli.setBorderLeft(BorderStyle.THIN);
                        cellStyleAppli.setBorderBottom(BorderStyle.THIN);
                        cellStyleAppli.setBorderRight(BorderStyle.THIN);
                        cellStyleAppli.setBorderTop(BorderStyle.THIN);

                        cellStyle3 = wb.createCellStyle();
                        createDataFormat1 = wb.createDataFormat();
                        cellStyle3.setDataFormat(createDataFormat1.getFormat("#,##0.00"));
                        cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
                        cellStyle3.setAlignment(HorizontalAlignment.LEFT);
                        cellStyle3.setFont(font);
                        cellStyle3.setBorderLeft(BorderStyle.THIN);
                        cellStyle3.setBorderBottom(BorderStyle.THIN);
                        cellStyle3.setBorderRight(BorderStyle.THIN);
                        cellStyle3.setBorderTop(BorderStyle.THIN);

                        cellStyle4 = wb.createCellStyle();
                        createDataFormat4 = wb.createDataFormat();
                        cellStyle4.setDataFormat(createDataFormat4.getFormat("#,##0"));
                        cellStyle4.setVerticalAlignment(VerticalAlignment.CENTER);
                        cellStyle4.setAlignment(HorizontalAlignment.CENTER);
                        cellStyle4.setFont(font2);
                        cellStyle4.setBorderLeft(BorderStyle.THIN);
                        cellStyle4.setBorderBottom(BorderStyle.THIN);
                        cellStyle4.setBorderRight(BorderStyle.THIN);
                        cellStyle4.setBorderTop(BorderStyle.THIN);
//						cellStyle = (XSSFCellStyle) wb.createCellStyle();
//						cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));

                    }
                    //if (!CollectionUtils.isEmpty(selectData) && maptitle != null) {
                    Integer totalFlg = null;
                    int f = 0;
                    int sizeTotal = selectData.size() + 1;
                    if (rowNo % 1000000 == 0) {
                        sheet = wb.createSheet(sheetName);
                        //int f = 0;
                        for (Map.Entry<String, String> entry : maptitle.entrySet()) {
                            String key = entry.getValue();
                            if (key.toUpperCase().contains("NAME") || key.toUpperCase().contains("DESCRIPTION")) {

                                sheet.setColumnWidth(f, 25 * 256);
                            } else if (key.replaceAll(" +", "").toUpperCase().contains("ORDERRELEASEMATRIX")) {
                                sheet.setColumnWidth(f, 28 * 256);
                            } else {
                                sheet.setColumnWidth(f, 13 * 256);

                            }
                            if ("Order Value".equals(key.trim())) {
                                totalFlg = f;
                            }
                            f++;
                        }

                        // sheet = wb.getSheetAt(rowNo / 1000000);
                        pageRowNo = 0;
                        nRow = sheet.createRow(pageRowNo); // 新建行对象
                        int n = 0;

                        for (Map.Entry<String, String> entry : maptitle.entrySet()) {
                            nCell = nRow.createCell(n++);
                            nCell.setCellValue(entry.getValue());
                            if (map.containsKey("Wei Zhu") || map.containsKey("Jason Song")
                                    || map.containsKey("Channel") || map.containsKey("Mingfei Guo")
                                    || map.containsKey("George Ge") || map.containsKey("Adam xu")
                                    || map.containsKey("Steve Vermant")
                                    || map.containsKey("Seaman Wu") || map.containsKey("Steve Vermant & CFO")) {

                                nCell.setCellStyle(headStyleAppli);
                            } else {

                                nCell.setCellStyle(headStyle);
                            }
                        }

                    }
                    if (!CollectionUtils.isEmpty(selectData) && maptitle != null) {
                        for (int k = 0; k < selectData.size(); k++) {
                            //sales team对应
                            if (selectData.get(k).containsKey("HEAD")) {
                                String saleHeadName = null;
                                if (selectData.get(k).get("HEAD") != null) {
                                    saleHeadName = selectData.get(k).get("HEAD").toString();
                                    if (saleHeadName.equalsIgnoreCase("Wei Zhu")) {
                                        selectData.get(k).put("HEAD", "Pharma");
                                    }
                                    if (saleHeadName.equalsIgnoreCase("Adam xu")) {
                                        selectData.get(k).put("HEAD", "Digital");
                                    }
                                    if (saleHeadName.equalsIgnoreCase("Channel")) {
                                        selectData.get(k).put("HEAD", "Channel");
                                    }
                                    if (saleHeadName.equalsIgnoreCase("Jason Song")) {
                                        selectData.get(k).put("HEAD", "Academia");
                                    }
                                    if (saleHeadName.equalsIgnoreCase("Mingfei Guo")) {
                                        selectData.get(k).put("HEAD", "Diagnostic");
                                    }
                                    if (saleHeadName.equalsIgnoreCase("Seaman Wu")) {
                                        selectData.get(k).put("HEAD", "AA I&T");
                                    }
                                    if (saleHeadName.equalsIgnoreCase("George Ge")) {
                                        selectData.get(k).put("HEAD", "Business B");
                                    }
                                    if (saleHeadName.equalsIgnoreCase("Steve Vermant")) {
                                        selectData.get(k).put("HEAD", "Business A");
                                    }
                                }
                            }
                            rowNo++;
                            nRow = sheet.createRow(++pageRowNo); // 新建行对象
                            int n = 0;
                            for (Map.Entry<String, String> entry : maptitle.entrySet()) {
                                String key = entry.getKey();
                                Object object = selectData.get(k).get(key);
                                nCell = nRow.createCell(n++);
                                if (object != null) {
                                    String s = object.toString();
                                    String s1 = s.replaceAll(" +", "").replace(",", "");
                                    if (s1.matches("^-?\\d+$") || s1.matches("^(-?\\d+)(\\.\\d+)?$")) {
                                        String s2 = s.replace(",", "");
                                        if ("-0".equals(s2.replaceAll(" +", ""))) {
                                            s2 = "0";
                                        }

                                        try {
                                            nCell.setCellValue(Double.valueOf(s2));
                                            String keys = entry.getKey();
                                            if (map.containsKey("Wei Zhu") || map.containsKey("Jason Song")
                                                    || map.containsKey("Channel") || map.containsKey("Mingfei Guo")
                                                    || map.containsKey("Seaman Wu") || map.containsKey("Seaman Wu")
                                                    || map.containsKey("George Ge") || map.containsKey("Steve Vermant")
                                                    || map.containsKey("Steve Vermant & CFO")) {

                                                if ("CREDIT_VALUE".equals(keys) || "DAYS_30_M".equals(keys)
                                                        || "OPEN_ORDER".equals(keys) || "CREDIT_LIMIT".equals(keys)
                                                        || "EXPOSURE".equals(keys) || "OVERRUNS".equals(keys)
                                                        || "RECEIVABLES".equals(keys)) {
                                                    nCell.setCellStyle(cellStyle4);
                                                } else {
                                                    nCell.setCellStyle(bodyStyleAppli);
                                                }

                                            } else {

                                                if (map.containsKey("block order" + target)
                                                        || map.containsKey("Release order" + target)
                                                        || map.containsKey("无放单意见") || map.containsKey("无欠款卡单")) {

                                                    if ("CC".equals(keys) || "DOCUMENT_NUMBER".equals(keys)
                                                            || "PARTNER".equals(keys)
                                                            || "EXTERNAL_REFER".equals(keys)) {
                                                        nCell.setCellStyle(bodyStyle);
                                                    } else {
                                                        nCell.setCellStyle(cellStyle3);
                                                    }

                                                }

                                            }

                                        } catch (Exception e) {
                                            nCell.setCellValue(s);
                                            System.out.println(s);
                                            System.out.println(s2);
                                        }
                                    } else if (s1.matches("^-?\\d+%$")) {

                                        if (map.containsKey("Wei Zhu") || map.containsKey("Jason Song")
                                                || map.containsKey("Channel") || map.containsKey("Mingfei Guo")
                                                || map.containsKey("Seaman Wu") || map.containsKey("Steve Vermant")
                                                || map.containsKey("George Ge") || map.containsKey("Adam xu")
                                                || map.containsKey("Steve Vermant & CFO")) {
                                            nCell.setCellStyle(cellStyleAppli);
                                        } else {

                                            nCell.setCellStyle(cellStyle2);
                                        }

                                        nCell.setCellValue(Double.valueOf(s.replace("%", "")) / 100);
                                        ;
                                    } else {
                                        nCell.setCellValue(s);
                                        String keys = entry.getKey();

                                        if (map.containsKey("Wei Zhu") || map.containsKey("Jason Song")
                                                || map.containsKey("Channel") || map.containsKey("Mingfei Guo")
                                                || map.containsKey("Seaman Wu") || map.containsKey("Steve Vermant")
                                                || map.containsKey("George Ge") || map.containsKey("Adam xu")
                                                || map.containsKey("Steve Vermant & CFO")) {

                                            if (keys.toUpperCase().contains("NAME")
                                                    || keys.toUpperCase().contains("DESCRIPTION")) {

                                                nCell.setCellStyle(bodyStyleAppli2);
                                            } else {

                                                nCell.setCellStyle(bodyStyleAppli);
                                            }
                                        } else {

                                            if (keys.toUpperCase().contains("NAME")
                                                    || keys.toUpperCase().contains("DESCRIPTION")) {

                                                nCell.setCellStyle(bodyStyle2);
                                            } else {

                                                nCell.setCellStyle(bodyStyle);
                                            }
                                        }


                                    }
                                } else {
                                    nCell.setCellValue("");
                                    nCell.setCellStyle(bodyStyle);
                                }

                            }

                        }
                        if (totalFlg != null) {
                            ExportUtil exportUtil = new ExportUtil(wb, sheet);
                            XSSFCellStyle headStyle2 = (XSSFCellStyle) exportUtil.getHeadStyleNoborderAndNopoint();

                            String colString = CellReference.convertNumToColString(totalFlg);
                            String sumstring = "SUM(" + colString + "2:" + colString + sizeTotal + ")";// 求和公式
                            nRow = sheet.createRow(++pageRowNo);

                            for (int i = 0; i < f; i++) {
                                if (i == (totalFlg - 2)) {
                                    nCell = nRow.createCell(totalFlg - 2 > 0 ? totalFlg - 2 : 0);
                                    nCell.setCellValue("Total");
                                    nCell.setCellStyle(headStyle2);
                                } else if (i == totalFlg) {
                                    nCell = nRow.createCell(totalFlg);
                                    nCell.setCellFormula(sumstring);
                                    nCell.setCellStyle(headStyle2);
                                } else {
                                    nCell = nRow.createCell(i);
                                    nCell.setCellValue("");
                                    nCell.setCellStyle(headStyle2);
                                }

                            }


                        }

                    }
                }
                if (wb != null) {
                    fOut = new FileOutputStream(exportFile);
                    wb.write(fOut);
                    fOut.flush();
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            log.error("导出excle异常", e);
        }

    }

    public Map<String, String> getMapperMap() {

        Map<String, String> map = new LinkedHashMap<String, String>();

        map.put("TRA_DAY", "交易日");
        map.put("BORROW", "借");
        map.put("LOAN", "贷");
        map.put("BALANCE", "余额");
        map.put("ABSTRACT", "摘要");
        map.put("RE_PAY_NAME", "收(付)方名称");
        map.put("RE_PAY_AC", "收(付)方帐号");
        map.put("TRA_TYPE", "交易类型");
        map.put("STACEY_YIN", "StaceyYin(凭证号)");
        map.put("SPHIL_YU", "PhilYu(凭证号)");
        map.put("NEED_CHECK", "Count > 1 need checking");
        map.put("ACCOUNT", "Account");
        map.put("PO", "PO");
        map.put("PAYER", "Payer");
        return map;

    }

    ;

}
