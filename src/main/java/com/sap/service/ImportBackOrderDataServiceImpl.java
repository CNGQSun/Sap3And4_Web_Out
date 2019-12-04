package com.sap.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sap.dao.BackOrderMapper;
import com.sap.utils.ExportUtil;

@Service("ImportDataImpl4")
public class ImportBackOrderDataServiceImpl implements ImportData {
	private static Logger log = LoggerFactory.getLogger(ImportBackOrderDataServiceImpl.class);

	@Autowired
	private BackOrderMapper mapper;

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

			val = val.replace(".", "").replaceAll(" +", "_").replace("0-30_Days", "DAYS_030")
					.replace("31-60_Days", "DAYS_3160").replace("61-90_Days", "DAYS_6190")
					.replace("=>_91_Days", "DAYS_91").replace("Utiliztn_%", "Utiliztn").replace("Util(&)", "Utiliztn")
					.replace("Limit", "CREDIT_LIMIT").replace("Credit_CREDIT_LIMIT", "CREDIT_LIMIT")
					.replace("Ext_ref", "EXTERNAL_REFER");

		} else {

			val = val.replace("（", "(").replace("）", ")").replaceAll("(\\r\\n|\\r|\\n|\\n\\r)+", "");
			if ("BLANK_ORDER".equalsIgnoreCase(tableName)) {
				val = val.replaceAll(" +", "").replace("交易日", "TRA_DAY").replace("借", "BORROW").replace("贷", "LOAN")
						.replace("余额", "BALANCE").replace("摘要", "ABSTRACT").replace("收(付)方名称", "RE_PAY_NAME")
						.replace("收(付)方帐号", "RE_PAY_AC").replace("交易类型", "TRA_TYPE")
						.replace("StaceyYin(凭证号)", "STACEY_YIN").replace("PhilYu(凭证号)", "SPHIL_YU")
						.replace("Count>1needchecking", "NEED_CHECK").replace("Account", "Account").replace("PO", "PO")
						.replace("Payer", "PAYER");
				val = "ID," + val;

			} else if ("SAP_MAPPING".equalsIgnoreCase(tableName)) {
				val = val.replace("收方名称", "name").replace("收(付)方名称", "name");
			}

		}
		String values = "";

		for (int i = 0; i < list.size(); i++) {
			List<String> listVal = list.get(i);

			String tempStr = "(";
			if ("BLANK_ORDER".equalsIgnoreCase(tableName) && !CollectionUtils.isEmpty(indexList)) {
				tempStr = tempStr + "'" + indexList.get(i) + "',";
			}

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
					if (string.equalsIgnoreCase("description")) {
						String[] split = value.split(" +");
						if (split.length >= 2) {
							value = split[1];
						}
					}

					if (string.equalsIgnoreCase("TotalAmt") || string.equalsIgnoreCase("贷")
							|| string.equalsIgnoreCase("余额")) {

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

	public void truncateTableData(String sql) {
		try {
			mapper.saveOrUpdateData(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("执行语句异常：", e);
		}

	}

	public void exportDataExcle(String sql, String path, String type) {
		try {
			if ("excle".equalsIgnoreCase(type)) {
				Workbook wb = null;
				CellStyle cellStyle1 = null;
				CellStyle cellStyle2 = null;
				Sheet sheet = null; // 工作表对象
				Row nRow = null; // 行对象
				Cell nCell = null; // 列对象
				FileOutputStream fOut = null;
				int rowNo = 0; // 总行号
				int pageRowNo = 0; // 页行号
				boolean flg = true;
				File exportFile = new File(path, "blank.xlsx");
				File parent = exportFile.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
				if (!exportFile.exists()) {
					exportFile.createNewFile();
				}
				Map<String, String> maptitle = getMapperMap();
				List<Map<String, Object>> selectData = null;
				if (StringUtils.isNoneBlank(sql)) {
					selectData = mapper.selectData(sql);
				}

				if (wb == null) {
					wb = new SXSSFWorkbook(100);
					cellStyle1 = wb.createCellStyle();
					cellStyle1.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
					cellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				// if (!CollectionUtils.isEmpty(selectData)) {

				if (rowNo % 1000000 == 0) {
					sheet = wb.createSheet("sheet-" + (rowNo / 1000000));
					sheet = wb.getSheetAt(rowNo / 1000000);
					pageRowNo = 0;
					nRow = sheet.createRow(pageRowNo); // 新建行对象
					ExportUtil exportUtil = new ExportUtil(wb, sheet);
					XSSFCellStyle headStyle = (XSSFCellStyle) exportUtil.getHeadStyle();
					int n = 0;

					for (Map.Entry<String, String> entry : maptitle.entrySet()) {
						nCell = nRow.createCell(n++);
						nCell.setCellValue(entry.getValue());
						nCell.setCellStyle(headStyle);
					}

				}
				if (!CollectionUtils.isEmpty(selectData)) {
					for (int k = 0; k < selectData.size(); k++) {
						rowNo++;
						nRow = sheet.createRow(++pageRowNo); // 新建行对象
						int n = 0;
						for (Map.Entry<String, String> entry : maptitle.entrySet()) {
							Object object = selectData.get(k).get(entry.getKey());
							String po = selectData.get(k).get("PO") == null ? ""
									: selectData.get(k).get("PO").toString();
							String partner = selectData.get(k).get("PAYER") == null ? ""
									: selectData.get(k).get("PAYER").toString();
							nCell = nRow.createCell(n++);
							nCell.setCellValue(object == null ? "" : object.toString());
							if (StringUtils.isNoneBlank(partner) && StringUtils.isBlank(po)
									&& "PAYER".equalsIgnoreCase(entry.getKey()))
								nCell.setCellStyle(cellStyle1);
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

	@Override
	public List<Map<String, Object>> selectData(String sql) {
		try {
			List<Map<String, Object>> selectData = mapper.selectData(sql);
			return selectData;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("执行语句异常：", e);
		}
		return null;
		
	};

}
