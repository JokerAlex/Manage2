package com.dzhy.manage.dao;

import com.dzhy.manage.entity.Produce;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduceMapper {
    int deleteByPrimaryKey(Long produceId);

    int insert(Produce record);

    int insertSelective(Produce record);

    Produce selectByPrimaryKey(Long produceId);

    int updateByPrimaryKeySelective(Produce record);

    int updateByPrimaryKey(Produce record);
}