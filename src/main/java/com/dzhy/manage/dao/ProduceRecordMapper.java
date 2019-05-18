package com.dzhy.manage.dao;

import com.dzhy.manage.entity.ProduceRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduceRecordMapper {
    int deleteByPrimaryKey(Long recordId);

    int insert(ProduceRecord record);

    int insertSelective(ProduceRecord record);

    ProduceRecord selectByPrimaryKey(Long recordId);

    int updateByPrimaryKeySelective(ProduceRecord record);

    int updateByPrimaryKey(ProduceRecord record);
}