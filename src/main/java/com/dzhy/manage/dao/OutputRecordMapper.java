package com.dzhy.manage.dao;

import com.dzhy.manage.entity.OutputRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface OutputRecordMapper {
    int deleteByPrimaryKey(Long recordId);

    int insert(OutputRecord record);

    int insertSelective(OutputRecord record);

    OutputRecord selectByPrimaryKey(Long recordId);

    int updateByPrimaryKeySelective(OutputRecord record);

    int updateByPrimaryKey(OutputRecord record);
}