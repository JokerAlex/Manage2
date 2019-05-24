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

    Produce selectByDateAndProductIdAndSukId(@Param("date") Integer date, @Param("productId") Integer productId,
                                             @Param("sukId") Integer sukId);

    int deleteBatch(@Param("list") List<Integer> produceIds);

    int deleteByDate(int dateInt);

    List<Produce> selectByConditions(@Param("date") Integer date, @Param("produceName") String produceName);

    int insertBatch(@Param("list") List<Produce> produceList);
}