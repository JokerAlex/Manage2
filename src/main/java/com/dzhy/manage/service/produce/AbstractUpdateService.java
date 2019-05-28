package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.dao.OutputMapper;
import com.dzhy.manage.dao.ProduceMapper;
import com.dzhy.manage.dao.ProduceRecordMapper;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.entity.ProduceRecord;
import com.dzhy.manage.enums.ProduceEnum;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName AbstractUpdateService
 * @Description 进度更新 service
 * @Author alex
 * @Date 2019-05-25
 **/
@Slf4j
public abstract class AbstractUpdateService {

    private OutputMapper outputMapper;
    private ProduceMapper produceMapper;
    private ProduceRecordMapper produceRecordMapper;

    @Autowired
    public void setOutputMapper(OutputMapper outputMapper) {
        this.outputMapper = outputMapper;
    }

    @Autowired
    public void setProduceMapper(ProduceMapper produceMapper) {
        this.produceMapper = produceMapper;
    }

    @Autowired
    public void setProduceRecordMapper(ProduceRecordMapper produceRecordMapper) {
        this.produceRecordMapper = produceRecordMapper;
    }

    public abstract Result update(Produce origin, int value, String comment, int flag);

    public abstract Result fix(Produce origin, int value, String comment);

    Output getOutput(Produce origin) {
        return this.getOutput(CommonUtil.getMonthToIntOfNow(), origin);
    }

    Output getOutput(int monthInt, Produce origin) {
        Output output = outputMapper.selectByMonthAndProductIdAndSukId(monthInt, origin.getProductId(), origin.getSukId());
        if (output != null) {
            return output;
        }
        output = Output.builder()
                .month(monthInt)
                .productId(origin.getProductId())
                .outputName(origin.getProduceName())
                .sukId(origin.getSukId())
                .sukPrice(origin.getSukPrice())
                .build();
        try {
            int count = outputMapper.insertSelective(output);
            log.info("insert output, count:{} outputId:{}", count, output.getOutputId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage() + "产值");
        }
        return output;
    }

    Result getResult(Produce origin, Produce update,
                     ProduceEnum enum1, int value1,
                     String comment) {
        ProduceRecord record = ProduceRecord.builder()
                .userId(CommonUtil.getUserIdFromContext())
                .productId(origin.getProductId())
                .sukId(origin.getSukId())
                .colName1(enum1.getName())
                .value1(value1)
                .colName2("")
                .value2(0)
                .colName3("")
                .value3(0)
                .comments(CommonUtil.getUserNameFromContext() + ":" + comment)
                .build();
        return getResult(update, record, null);
    }

    Result getResult(Produce origin, Produce update, Output outputUpdate,
                     String colName1, int value1,
                     String colName2, int value2,
                     String colName3, int value3,
                     String comment) {
        ProduceRecord record = ProduceRecord.builder()
                .userId(CommonUtil.getUserIdFromContext())
                .productId(origin.getProductId())
                .sukId(origin.getSukId())
                .colName1(colName1)
                .value1(value1)
                .colName2(colName2)
                .value2(value2)
                .colName3(colName3)
                .value3(value3)
                .comments(CommonUtil.getUserNameFromContext() + ":" + comment)
                .build();
        return getResult(update, record, outputUpdate);
    }

    private Result getResult(Produce update, ProduceRecord record, Output outputUpdate) {
        try {
            int count = produceMapper.updateByPrimaryKeySelective(update);
            log.info("update produce, count:{}, produceId:{}", count, update.getProduceId());
            if (outputUpdate != null) {
                count = outputMapper.updateByPrimaryKeySelective(outputUpdate);
                log.info("update output, count:{}, outputId:{}", count, outputUpdate.getOutputId());
            }
            count = produceRecordMapper.insertSelective(record);
            log.info("insert produceRecord, count:{}, recordId:{}", count, record.getRecordId());
        } catch (Exception e) {
            log.error("update produce error", e);
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }
}
