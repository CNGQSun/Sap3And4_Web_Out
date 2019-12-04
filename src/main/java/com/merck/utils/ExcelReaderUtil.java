/**
 * Copyright (C), 2018-2018, XXX有限公司
 * FileName: ExcelReaderUtil
 * Author:   AAS lei
 * Date:     2018/6/26 14:19
 * Description:
 */
package com.merck.utils;

import static org.apache.poi.poifs.filesystem.FileMagic.OLE2;
import static org.apache.poi.poifs.filesystem.FileMagic.OOXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;

import com.merck.service.ImportData;


public class ExcelReaderUtil {

    public static String EXCEL_V2007="excel_v2007";
    public static String EXCEL_V2003="excel_v2003";
    public static String UNKNOWN_TYPE="unknown_type";

    public static String checkExcelVersion(File file) throws Exception {
        FileInputStream is=null;
        try {
            is = new FileInputStream(file);
            InputStream ips = FileMagic.prepareToCheckMagic(is);
            FileMagic fm = FileMagic.valueOf(ips);
            if (fm == OLE2) {
                NPOIFSFileSystem fs = new NPOIFSFileSystem(ips);
                DirectoryNode root = fs.getRoot();
                if (root.hasEntry(Decryptor.DEFAULT_POIFS_ENTRY)) {
                    return ExcelReaderUtil.EXCEL_V2007;
                }else {
                    return ExcelReaderUtil.EXCEL_V2003;
                }
            }else if (fm == OOXML){
                return ExcelReaderUtil.EXCEL_V2007;
            }else {
                return ExcelReaderUtil.UNKNOWN_TYPE;
            }
        } catch (Exception e) {
            throw e;
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取Excel文件
     * @param
     * @throws Exception
     */
    public static void readExcel(ImportData reader, File file,String tableName) throws Exception {
        try {
            String excelVersion = checkExcelVersion(file);
            if (ExcelReaderUtil.EXCEL_V2007.equals(excelVersion)){
                System.out.println("**********开始解析excel2007**********");
                Excel2007Reader excel07 = new Excel2007Reader();
                excel07.setRowReader(reader,tableName);
                excel07.process(file);
            }else {
             //   throw new RuntimeException("The Excel version is neither 07 nor 03, unable to process this file.");
                throw new RuntimeException("excle格式不正确，请上传正确格式的excle文件！");
            }
        } catch (Exception e) {
            throw e;
        }
    }



}
