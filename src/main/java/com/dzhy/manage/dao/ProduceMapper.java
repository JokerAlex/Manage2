package com.dzhy.manage.dao;

import com.dzhy.manage.entity.Produce;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduceMapper {
    int deleteByPrimaryKey(Long produceId);

    int insert(Produce record);

    int insertSelective(Produce record);

    Produce selectByPrimaryKey(Long produceId);

    int updateByPrimaryKeySelective(Produce record);

    int updateByPrimaryKey(Produce record);

    List<Produce> selectBySukId(int sukId);

    int updatePrice(@Param("sukId") int sukId, @Param("price") float price);

    Produce selectByIdAndDate(@Param("productId") Integer productId, @Param("date") Integer date);
}