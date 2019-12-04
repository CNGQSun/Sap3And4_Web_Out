package com.sap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.FileCopyUtils;

import com.AppMainAll;
import com.merck.utils.DateUtils;
import com.sap.model.SqlStr;
import com.sap.service.ImportData;
import com.sap.utils.CSVUtil;
import com.sap.utils.ExcelReaderUtil;

@SpringBootApplication
@MapperScan("com.*.dao")
public class Test4 {
 private static	Logger log=LoggerFactory.getLogger(Test4.class);
	public static final Properties p = new Properties();
	public static void main(String[] args) {
		getParamDetails(args);
		ConfigurableApplicationContext run = SpringApplication.run(Test4.class, args);
		ImportData bean = (ImportData) run.getBean("ImportDataImpl4");
		test(bean);
		
	}
	
	public static void test(ImportData bean4) {


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
			
			bean4.exportDataExcle(SqlStr.export_data, exportPath, "excle");
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

			if (blankOrderFile != null && blankOrderFile.exists()) {
				String name = blankOrderFile.getName();
				String string = BackUpPath + name.replace(".xlsx", suf + ".xlsx");
				File fileCopy = new File(string);
				flgBank = blankOrderFile.renameTo(fileCopy);
				log.info("Sap4.4备份移动bank.xlsx到" + fileCopy.getAbsolutePath() + (flgBank ? " 成功！ " : " 失败！ "));
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
			e.printStackTrace();
			log.error("Sap4.4执行出异常：时间" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), e);
		}

	
	}
	
	public static void getParamDetails(String[] args) {
		try {
			String path=AppMainAll.class.getClassLoader().getResource("config.properties").getPath();
			if(args!=null&&args.length!=0&&StringUtils.isNoneBlank(args[0])) {
				path = args[0];
			}
			InputStream resourceAsStream = new FileInputStream(new File(path));
			p.load(resourceAsStream);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("参数启动异常",e);
		}
	}

}
