/**
 * Copyright (C), 2018-2018, XXX有限公司
 * FileName: Excel2007Reader
 * Author:   AAS lei
 * Date:     2018/6/26 11:20
 * Description:
 */
package com.merck.utils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.merck.service.ImportData;

/**
 * 抽象Excel2007读取器，excel2007的底层数据结构是xml文件，采用SAX的事件驱动的方法解析
 * xml，需要继承DefaultHandler，在遇到文件内容时，事件会触发，这种做法可以大大降低
 * 内存的耗费，特别使用于大数据量的文件。
 */
public class Excel2007Reader extends DefaultHandler {
    private Logger logger = Logger.getLogger(Excel2007Reader.class);

    private static StylesTable stylesTable;
    //共享字符串表
    private MyShardingTable sst;
    //    private ReadOnlySharedStringsTable sst;
//    private SharedStringsTable sst;
    //上一次的内容
    private String lastContents;
    private String lastTag;
    private boolean nextIsString;
    private int sheetIndex = -1;
    private List<String> rowlist = new ArrayList<String>();
    //private List<String> indexlist = new ArrayList<String>();
    private int indexFlg=0;
    //每个批次返回行数量
    private int batchNum = 500;
    private List<List<String>> rows = new ArrayList<List<String>>();
    private List<String> title = new ArrayList<String>();
    private Map<Integer,String> mapTitle = new LinkedHashMap<Integer,String>();

    public Excel2007Reader() {
    }

    //当前行
    private int curRow = 0;
    //当前列
    private int curCol = 0;
    private CellDataType nextDataType = CellDataType.SSTINDEX;
    private final DataFormatter formatter = new DataFormatter();
    private short formatIndex;
    private String formatString;

    //用一个enum表示单元格可能的数据类型
    enum CellDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
    }

    private boolean isTElement;
    //有效数据矩形区域,A1:Y2
    private String dimension;
    //根据dimension得出每行的数据长度
    private int longest;
    //上个有内容的单元格id，判断空单元格
    private String lastRowid;
    private ImportData rowReader;
    
    private String tableName;

    public void setRowReader(ImportData rowReader,String tableName) {
        this.rowReader = rowReader;
        this.tableName=tableName;
    }

    public List<String> getTitle() {
        return title;
    }

    /**
     * 只遍历一个电子表格，其中sheetId为要遍历的sheet索引，从1开始，1-3
     *
     * @param filename
     * @param sheetId
     * @throws Exception
     */
    public void processOneSheet(String filename, int sheetId) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename, PackageAccess.READ);
        XSSFReader r = new XSSFReader(pkg);
        ReadOnlySharedStringsTable sst = new ReadOnlySharedStringsTable(pkg);
        XMLReader parser = null;
        // 根据 rId# 或 rSheet# 查找sheet
        InputStream sheet2 = r.getSheet("rId" + sheetId);
        sheetIndex++;
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }

    /**
     * 遍历工作簿中所有的电子表格
     *
     * @param
     * @throws Exception
     */
    public void process(String filename) throws Exception {
        OPCPackage p = OPCPackage.open(filename, PackageAccess.READ);
        XSSFReader xssfReader = new XSSFReader(p);
        MyShardingTable strings = new MyShardingTable(p, 100);
        stylesTable = xssfReader.getStylesTable();

        XMLReader parser = fetchSheetParser(strings);
        Iterator<InputStream> sheets = xssfReader.getSheetsData();
        while (sheets.hasNext()) {
            curRow = 0;
            sheetIndex++;
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
    }

    /**
     * 遍历工作簿中所有的电子表格
     *
     * @param
     * @throws Exception
     */
    public void process(File file) throws Exception {
        OPCPackage p = null;
        try {
            p = OPCPackage.open(file, PackageAccess.READ);

            XSSFReader xssfReader = new XSSFReader(p);
            MyShardingTable strings = new MyShardingTable(p, 100);
            stylesTable = xssfReader.getStylesTable();
            XMLReader parser = fetchSheetParser(strings);
            Iterator<InputStream> sheets = xssfReader.getSheetsData();
            boolean b = sheets.hasNext();
            if (!b){
                //throw new RuntimeException("It may not be a Excel07 file. Please check the file!");
                throw new RuntimeException("excle解析失败，请上传正确格式的excle文件！");
            }
            
            if("PY_SALES_MAPPING".equals(tableName)) {
                while (b) {
                	InputStream sheet = sheets.next();
                	if(((org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator) sheets).getSheetName().equals("SP_PY")){
                		InputSource sheetSource = new InputSource(sheet);
       	                parser.parse(sheetSource);
                		sheet.close();
                		b = sheets.hasNext();
                		//读取指定表格退出
                		break;
                	}
                }

            }else if("SIGMA".equals(tableName)){
                while (b) {
                	InputStream sheet = sheets.next();
                	if(((org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator) sheets).getSheetName().equals("Mapping list")){
                		InputSource sheetSource = new InputSource(sheet);
       	                parser.parse(sheetSource);
                		sheet.close();
                		b = sheets.hasNext();
                		//读取指定表格退出
                		break;
                	}
                }
            }else if("ZCOP_AGING".equals(tableName)){
                while (b) {
                	InputStream sheet = sheets.next();
                	if(((org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator) sheets).getSheetName().equals("Aging Raw Data")){
                		InputSource sheetSource = new InputSource(sheet);
       	                parser.parse(sheetSource);
                		sheet.close();
                		b = sheets.hasNext();
                		//读取指定表格退出
                		break;
                	}
                }
            }else if("ORDERING".equals(tableName)){
                while (b) {
                	InputStream sheet = sheets.next();
                	if(((org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator) sheets).getSheetName().equals("Sheet1")){
                		InputSource sheetSource = new InputSource(sheet);
       	                parser.parse(sheetSource);
                		sheet.close();
                		b = sheets.hasNext();
                		//读取指定表格退出
                		break;
                	}
                }
            }else {
            
	            while (b) {
	                curRow = 0;
	                sheetIndex++;
	                InputStream sheet = sheets.next();
	                InputSource sheetSource = new InputSource(sheet);
	                parser.parse(sheetSource);
	                sheet.close();
	                b = sheets.hasNext();
	                //读取第一个表格就退出
	                break;
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (p != null) {
                p.close();
            }
        }

    }

    public XMLReader fetchSheetParser(MyShardingTable sst)
            throws SAXException {
        XMLReader parser = XMLReaderFactory
                .createXMLReader("org.apache.xerces.parsers.SAXParser");
        this.sst = sst;
        parser.setContentHandler(this);
        return parser;
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {

        if (name.equals("dimension")) {
            dimension = attributes.getValue("ref");
            longest = covertRowIdtoInt(dimension.substring(dimension.indexOf(":") + 1));
        }
        // c => 单元格
        if ("c".equals(name)) {
            String rowId = attributes.getValue("r");
            //空单元判断，添加空字符到list
            if (lastRowid != null) {
                int gap = covertRowIdtoInt(rowId) - covertRowIdtoInt(lastRowid);
                for (int i = 0; i < gap - 1; i++) {
                    rowlist.add(curCol, "");
                    curCol++;
                }
            } else {
                //第一个单元格可能不是在第一列
                if (!"A1".equals(rowId)) {
                    for (int i = 0; i < covertRowIdtoInt(rowId) - 1; i++) {
                        rowlist.add(curCol, "");
                        curCol++;
                    }
                }
            }

            lastRowid = rowId;
            this.setNextDataType(attributes);
            // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
            String cellType = attributes.getValue("t");
            if ("s".equals(cellType)) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }
        }
        //当元素为t时
        if ("t".equals(name)) {
            isTElement = true;
        } else {
            isTElement = false;
        }
        //记录上一个标签名
        lastTag=name;
        // 置空
        lastContents = "";
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        //t元素也包含字符串
        if (isTElement) {
            String value = lastContents.trim();
            rowlist.add(curCol, value);
            curCol++;
            isTElement = false;
            // v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
            // 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
        } else if ("v".equals(name)) {
            String value = this.getDataValue(lastContents.trim(), "");
            rowlist.add(curCol, value);
            curCol++;
            //判断c标签自结束<c/>
        } else if ("c".equals(lastTag)&&"c".equals(name)){
            rowlist.add(curCol, "");
            curCol++;
        }else {
            //如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
            if (name.equals("row")) {
                //判断最后一个单元格是否在最后，补齐列数
                if (covertRowIdtoInt(lastRowid) < longest) {
                    for (int i = 0; i < longest - covertRowIdtoInt(lastRowid); i++) {
                        rowlist.add(curCol, "");
                        curCol++;
                    }
                }
                try {
                    //第一行数据作为标题
                    if (curRow <= 0 && rowlist.size() > 0) {
                        this.title.clear();
                        this.title.addAll(rowlist);
                        rowlist.clear();
                        for (int i = 0; i < title.size(); i++) {
							if(StringUtils.isNoneBlank(title.get(i))) {
								mapTitle.put(i, title.get(i).trim());
							}
						} 
                    }
                    //去除第一行数据curRow>0
                    if (curRow > 0 && rowlist.size() > 0) {
                    	boolean flg=false;
                    	for (String string : rowlist) {
                    		if(StringUtils.isNoneBlank(string)) {
                    			flg=true;
                    			break;
                    		}
						}
                    	if(flg) {
	                        List<String> list = new ArrayList<String>();
	                        list.addAll(rowlist);
	                        rows.add(list);
	                        int index=   indexFlg++;
	                        //indexlist.add(index+"");
                        }
                    }
                    if (batchNum > 0 && rows.size() > 0 && rows.size() % batchNum == 0) {
                        rowReader.insertData(rows, mapTitle,"excle",tableName,null);
                        rows.clear();
                        //indexlist.clear();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                rowlist.clear();
                curRow++;
                curCol = 0;
                lastRowid = null;
            }
        }
        lastTag=null;
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            if (rows.size() > 0) {
            	rowReader.insertData(rows, mapTitle,"excle",tableName,null);
                rows.clear();
               // indexlist.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        //得到单元格内容的值
        lastContents += new String(ch, start, length);
    }


    /**
     * 处理数据类型
     *
     * @param attributes
     */
    public void setNextDataType(Attributes attributes) {
        nextDataType = CellDataType.NUMBER;
//        nextDataType = CellDataType.SSTINDEX;
        formatIndex = -1;
        formatString = null;
        String cellType = attributes.getValue("t");
        String cellStyleStr = attributes.getValue("s");
        String columData = attributes.getValue("r");

        if ("b".equals(cellType)) {
            nextDataType = CellDataType.BOOL;
        } else if ("e".equals(cellType)) {
            nextDataType = CellDataType.ERROR;
        } else if ("inlineStr".equals(cellType)) {
            nextDataType = CellDataType.INLINESTR;
        } else if ("s".equals(cellType)) {
            nextDataType = CellDataType.SSTINDEX;
        } else if ("str".equals(cellType)) {
            nextDataType = CellDataType.FORMULA;
        }

        if (cellStyleStr != null) {
            int styleIndex = Integer.parseInt(cellStyleStr);
            XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
            formatIndex = style.getDataFormat();
            formatString = style.getDataFormatString();
            System.out.println("formatIndex="+formatIndex+" , formatString="+formatString);
            if (DateUtil.isADateFormat(formatIndex,formatString)|| 30<=formatIndex&&formatIndex<=33 || 55<=formatIndex&&formatIndex<=58 ){//带有中文格式的无法识别
                nextDataType=CellDataType.DATE;
            }else if (formatString == null) {
                nextDataType = CellDataType.NULL;
                formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
            }
        }
    }

    /**
     * 对解析出来的数据进行类型处理
     *
     * @param value   单元格的值（这时候是一串数字）
     * @param thisStr 一个空字符串
     * @return
     */
    @SuppressWarnings("deprecation")
    public String getDataValue(String value, String thisStr) {
        switch (nextDataType) {
            // 这几个的顺序不能随便交换，交换了很可能会导致数据错误
            case BOOL:
                char first = value.charAt(0);
                thisStr = first == '0' ? "FALSE" : "TRUE";
                break;
            case ERROR:
                thisStr = "\"ERROR:" + value.toString() + '"';
                break;
            case FORMULA:
                thisStr = '"' + value.toString() + '"';
                break;
            case INLINESTR:
                XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());

                thisStr = rtsi.toString();
                rtsi = null;
                break;
            case SSTINDEX:
                String sstIndex = value.toString();
                try {
                    int idx = Integer.parseInt(sstIndex);
                    XSSFRichTextString rtss = new XSSFRichTextString(sst.getEntryAt(idx));
                    thisStr = rtss.toString();
                    rtss = null;
                } catch (NumberFormatException ex) {
                    thisStr = value.toString();
                }
                break;
            case NUMBER:
                String n = value.toString();
                if (this.formatString != null) {
                    thisStr = this.formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex, this.formatString);//
                } else {
                    thisStr = n;
                }
                thisStr = thisStr.replace("_", "").trim();
                break;
            case DATE:
                Date date = DateUtil.getJavaDate(Double.parseDouble(value));
//              System.out.println("date类型:"+date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                thisStr = sdf.format(date);
                // 对日期字符串作特殊处理
                thisStr = thisStr.replace("T", " ");
                break;
            default:
                thisStr = " ";
                break;
        }
        return thisStr;
    }

    /**
     * 列号转数字   AB7-->28 第28列
     *
     * @param rowId
     * @return
     */
    public int covertRowIdtoInt(String rowId) {
        int firstDigit = -1;
        for (int c = 0; c < rowId.length(); ++c) {
            if (Character.isDigit(rowId.charAt(c))) {
                firstDigit = c;
                break;
            }
        }
        //AB7-->AB
        //AB是列号, 7是行号
        String newRowId = rowId.substring(0, firstDigit);
        int num = 0;
        int result = 0;
        int length = newRowId.length();
        for (int i = 0; i < length; i++) {
            //先取最低位，B
            char ch = newRowId.charAt(length - i - 1);
            //B表示的十进制2，ascii码相减，以A的ascii码为基准，A表示1，B表示2
            num = (int) (ch - 'A' + 1);
            //列号转换相当于26进制数转10进制
            num *= Math.pow(26, i);
            result += num;
        }
        return result;

    }
    
}
