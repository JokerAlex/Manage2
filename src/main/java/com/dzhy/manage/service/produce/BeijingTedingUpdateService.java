package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.enums.OutputEnum;
import com.dzhy.manage.enums.ProduceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName BeijingTedingUpdateService
 * @Description 北京特定更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class BeijingTedingUpdateService extends AbstractUpdateService {

    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        if (flag == Constants.NOT_OUTPUT) {
            return this.isNotOutput(origin, value, comment);
        } else {
            return this.isOutput(origin, value, comment);
        }
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .beijingTeding(value)
                .build();
        return getResult(origin, update, ProduceEnum.BEIJING_TEDING, value, comment);
    }

    /**
     * 进度：北京特定增加，特定减少
     * 产值：特定增加
     *
     * @param origin
     * @param value
     * @param comment
     * @return
     */
    private Result isNotOutput(Produce origin, int value, String comment) {
        if (origin.getTeDing() - value < 0) {
            log.info("origin.getTeDing() - value = {}", origin.getTeDing() - value);
            return Result.isError("特定库存不足");
        }
        if (origin.getBeijingTeding() + value < 0) {
            log.info("origin.getBeijingTeding() + value = {}", origin.getBeijingTeding() + value);
            return Result.isError("退单量超过北京特定库存");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getTeDing() + value < 0) {
            log.info("outputOrigin.getTeDing() + value = {}", outputOrigin.getTeDing() + value);
            return Result.isError("退单后特定产值为负值");
        }
        if (outputOrigin.getBeijingTedingInput() + value < 0) {
            log.info("outputOrigin.getBeijingTedingInput() + value = {}", outputOrigin.getBeijingTedingInput() + value);
            return Result.isError("退单后北京特定入库为负值");
        }
        if (outputOrigin.getBeijingTedingStock() + value < 0) {
            log.info("outputOrigin.getBeijingTedingStock() + value = {}", outputOrigin.getBeijingTedingStock() + value);
            return Result.isError("退单后北京特定剩余为负值");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .beijingTeding(origin.getBeijingTeding() - value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .teDing(outputOrigin.getTeDing() + value)
                .beijingTedingInput(outputOrigin.getBeijingTedingInput() + value)
                .beijingTedingStock(outputOrigin.getBeijingTedingStock() + value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.BEIJING_TEDING.getName(), value,
                ProduceEnum.TE_DING.getName(), -value,
                //北京特定入库，北京特定库存
                OutputEnum.BEIJING_TEDING_INPUT.getName(), value,
                comment);
    }

    /**
     * 出货
     * 进度：北京特定减少
     * 产值：北京特定剩余减少
     *
     * @param origin
     * @param value
     * @param comment
     * @return
     */
    private Result isOutput(Produce origin, int value, String comment) {
        if (origin.getBeijingTeding() - value < 0) {
            log.info("origin.getBeijingTeding() - value = {}", origin.getBeijingTeding() - value);
            return Result.isError("北京特定库存不足");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getBeijingTedingStock() - value < 0) {
            log.info("outputOrigin.getBeijingTedingStock() - value = {}", outputOrigin.getBeijingTedingStock() - value);
            return Result.isError("北京特定库存不足");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .beijingTeding(origin.getBeijingTeding() - value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .beijingTedingStock(outputOrigin.getBeijingTedingStock() - value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.BEIJING_TEDING.getName(), -value,
                OutputEnum.BEIJING_TEDING_STOCK.getName(), -value,
                "", 0,
                comment);
    }
}
