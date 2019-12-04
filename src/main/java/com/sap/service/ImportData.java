package com.sap.service;

import java.util.List;
import java.util.Map;

public interface ImportData {
  
	public void insertData(List<List<String>> list,Map<Integer,String> titleCsv,String type,String tableName,List<String> indexList);
	public void exportDataExcle(String sql,String path,String type);
	public void truncateTableData(String sql);
	public List< Map<String,Object>> selectData(String sql);
	
	
	
}