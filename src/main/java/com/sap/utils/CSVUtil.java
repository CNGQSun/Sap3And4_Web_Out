/**
 * Copyright (C), 2018-2018, XXX有限公司
 * FileName: CSVUtils
 * Author:   AAS lei
 * Date:     2018/6/28 18:41
 * Description:
 */
package com.sap.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.sap.service.ImportData;

public class CSVUtil {
	private Logger logger = Logger.getLogger(CSVUtil.class);

	/**
	 * 导出
	 *
	 * @param file     csv文件(路径+文件名)
	 * @param dataList 数据
	 * @return
	 */
	public static boolean write(File file, List<String> dataList, String charset) throws Exception {
		boolean isSucess = false;
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(file, true);
			osw = new OutputStreamWriter(out, charset);
			bw = new BufferedWriter(osw);
			if (dataList != null && !dataList.isEmpty()) {
				// 追加新数据
				for (String data : dataList) {
					bw.append(data).append(System.getProperty("line.separator"));
				}
			}
			isSucess = true;
		} catch (Exception e) {
			e.printStackTrace();
			isSucess = false;
			throw e;
		} finally {
			if (bw != null) {
				try {
					bw.close();
					bw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
					osw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return isSucess;
	}

	// 去掉字符串前后双引号
	public static String clearStartAndEndQuote(String str) {
		if (str != null && str.length() >= 2) {
			if (str.indexOf("\"") == 0 && str.lastIndexOf("\"") == (str.length() - 1)) {
				str = str.substring(1, str.length()); // 去掉第一个 "
				str = str.substring(0, str.length() - 1); // 去掉最后一个 "
			}
			str = str.replace("\"\"", "\"");// 把两个双引号换成一个双引号
		}
		return str;
	}

	public static int readCsv(File file, String charset, List<String> title, ImportData service,String tableName) throws Exception {

		BufferedReader br = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		String line = "";
		boolean isTitleCloum = false;
		List<List<String>> rows = new ArrayList<List<String>>();
		Map<Integer,String> titleCsv = new LinkedHashMap<Integer,String>();
		int curRow = 0;
		int curRow2 = 0;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, charset);
			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				String[] cells = line.split("\t");
				List<String> row = new ArrayList<String>();
				for (String c : cells) {
					System.out.println(c);
					String s = CSVUtil.clearStartAndEndQuote(c);
					row.add(s);
				}
				if (!isTitleCloum) {
					if (row.containsAll(title)) {
						curRow2 = curRow;
						for (int i = 0; i < cells.length; i++) {
							if(StringUtils.isNoneBlank(row.get(i))) {
								titleCsv.put(i, row.get(i));
							}
						} 
						
						isTitleCloum = true;
					} else {
						curRow++;
						continue;
					}
				}

				// 加入到行集合中
				if (curRow > curRow2 && row.size() > 0) {
					boolean flg = false;
					for (String string : row) {
						if (StringUtils.isNoneBlank(string)) {
							flg = true;
							break;
						}
					}
					if (flg) {
						rows.add(row);
					}

					if (!CollectionUtils.isEmpty(rows) && rows.size() % 200 == 0) {
						service.insertData(rows, titleCsv,"csv",tableName,null);
						rows.clear();
					}

				}
				curRow++;
			}

			if (!CollectionUtils.isEmpty(rows)) {
				service.insertData(rows, titleCsv,"csv",tableName,null);
				rows.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
					isr = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return curRow;
	}



}
