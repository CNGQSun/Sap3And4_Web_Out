package com.merck.service;

import java.util.List;
import java.util.Map;

public interface ImportData {
  
	public void insertData(List<List<String>> list,Map<Integer,String> titleCsv,String type,String tableName,List<String> indexList);
	public void exportDataExcle(Map<String,String> map,String path,String type,Map<String,Map<String,String>> mapTitles,String timeFlg);
	public void truncateTableData(String sql);
	
	
}