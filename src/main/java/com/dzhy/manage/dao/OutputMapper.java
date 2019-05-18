package com.dzhy.manage.dao;

import com.dzhy.manage.entity.Output;
import org.springframework.stereotype.Repository;

@Repository
public interface OutputMapper {
    int deleteByPrimaryKey(Long outputId);

    int insert(Output record);

    int insertSelective(Output record);

    Output selectByPrimaryKey(Long outputId);

    int updateByPrimaryKeySelective(Output record);

    int updateByPrimaryKey(Output record);
}