package com.sap.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sap.model.BackOrder;

@Mapper
public interface BackOrderMapper {
    int insert(BackOrder record);

    int insertSelective(BackOrder record);
    
    void saveOrUpdateData (@Param("sql") String sql);
    List< Map<String,Object>> selectData (@Param("sql") String sql);
}