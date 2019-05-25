package com.dzhy.manage.dao;

import com.dzhy.manage.entity.Output;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutputMapper {
    int deleteByPrimaryKey(Long outputId);

    int insert(Output record);

    int insertSelective(Output record);

    Output selectByPrimaryKey(Long outputId);

    int updateByPrimaryKeySelective(Output record);

    int updateByPrimaryKey(Output record);

    int updatePrice(@Param("sukId") int sukId, @Param("price") float price);

    List<Output> selectByConditions(@Param("month") int month, @Param("productName") String productName);

    Output selectByMonthAndProductIdAndSukId(@Param("month") int monthInt,
                                             @Param("productId") Integer productId, @Param("sukId") Integer sukId);
}