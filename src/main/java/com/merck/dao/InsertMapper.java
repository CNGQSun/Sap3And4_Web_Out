package com.merck.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface InsertMapper {
    void saveOrUpdateData (@Param("sql") String sql);
    List< Map<String,Object>> selectData (@Param("sql") String sql);
}