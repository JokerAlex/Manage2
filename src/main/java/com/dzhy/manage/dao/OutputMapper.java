package com.dzhy.manage.dao;

import com.dzhy.manage.entity.Output;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutputMapper {
    int deleteByPrimaryKey(Long outputId);

    int insert(Output record);

    int insertSelective(Output record);

    Output selectByPrimaryKey(Long outputId);

    int updateByPrimaryKeySelective(Output record);

    int updateByPrimaryKey(Output record);

    int updatePrice(@Param("sukId") int sukId, @Param("price") float price);
}