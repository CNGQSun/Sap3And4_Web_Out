package com.merck.utils;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class ExportUtil {
	private Workbook wb = null;

	private Sheet sheet = null;

	/**
	 * @param wb
	 * @param sheet
	 */
	public ExportUtil(Workbook wb, Sheet sheet) {
		this.wb = wb;
		this.sheet = sheet;
	}

	/**
	 * 合并单元格后给合并后的单元格加边框
	 * 
	 * @param region
	 * @param cs
	 */
	public void setRegionStyle(CellRangeAddress region, XSSFCellStyle cs) {

		int toprowNum = region.getFirstRow();
		for (int i = toprowNum; i <= region.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				Cell cell = row.getCell(j);// XSSFCellUtil.getCell(row,
											// (short) j);
				cell.setCellStyle(cs);
			}
		}
	}

	/**
	 * 设置表头的单元格样式
	 * 
	 * @return
	 */
	public CellStyle getHeadStyle() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为淡蓝色
		cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		// cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		font.setBold(true);
		font.setFontName("微软雅黑");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		return cellStyle;
	}
	/**
	 * 设置表头的单元格样式
	 * 
	 * @return
	 */
	public CellStyle getHeadStyleForAppli() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为淡蓝色
		cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		// cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		font.setBold(true);
		font.setFontName("微软雅黑");
		font.setFontHeight((short) 220);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		return cellStyle;
	}

	public CellStyle getHeadStyleNoborder() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为淡蓝色
		cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		// cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		font.setBold(true);
		font.setFontName("宋体");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条

		return cellStyle;
	}

	public CellStyle getHeadStyleNoborderAndNopoint() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为淡蓝色
		cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		// cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);

		DataFormat createDataFormat = wb.createDataFormat();
		cellStyle.setDataFormat(createDataFormat.getFormat("#,##0"));

		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		font.setBold(true);
		font.setFontName("微软雅黑");
		font.setFontHeight((short) 220);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		return cellStyle;
	}

	/**
	 * 设置表体的单元格样式
	 * 
	 * @return
	 */
	public CellStyle getBodyStyle() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		// cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		// font.setBold(true);
		font.setFontName("微软雅黑");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		return cellStyle;
	}
	public CellStyle getBodyStyleAppli() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		// cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		// font.setBold(true);
		font.setFontName("微软雅黑");
		font.setFontHeight((short) 220);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		return cellStyle;
	}

	public CellStyle getBodyStyleBord() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		// cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		// font.setBold(true);
		font.setFontName("微软雅黑");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		// 设置单元格边框为细线条
		return cellStyle;
	}

	public CellStyle getBodyStyleAndLeft() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		// cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		// font.setBold(true);
		font.setFontName("微软雅黑");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		return cellStyle;
	}
	public CellStyle getBodyStyleLeftAppli() {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		// cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		// font.setBold(true);
		font.setFontName("微软雅黑");
		font.setFontHeight((short) 220);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		// 设置单元格边框为细线条
		return cellStyle;
	}
	
	
	
	
	
	//----------------------------------------------------------------------------
	
	
	
	
	
	
	
	
	
	
	
}
