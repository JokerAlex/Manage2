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
 * @ClassName BeijingUpdateService
 * @Description 北京更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class BeijingUpdateService extends AbstractUpdateService {

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
                .beijing(value)
                .build();
        return getResult(origin, update, ProduceEnum.BEIJING, value, comment);
    }

    /**
     * 进度：北京增加，包装减少
     * 产值：包装增加
     *
     * @param origin
     * @param value
     * @param comment
     * @return
     */
    private Result isNotOutput(Produce origin, int value, String comment) {
        if (origin.getBaoZhuang() - value < 0) {
            log.info("origin.getBaoZhuang() - value = {}", origin.getBaoZhuang() - value);
            return Result.isError("包装库存不足");
        }
        if (origin.getBeijing() + value < 0) {
            log.info("origin.getBeijing() + value = {}", origin.getBeijing() + value);
            return Result.isError("退单量超过北京库存");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getBaoZhuang() + value < 0) {
            log.info("outputOrigin.getBaoZhuang() + value = {}", outputOrigin.getBaoZhuang() + value);
            return Result.isError("退单后包装产值为负值");
        }
        if (outputOrigin.getBeijingInput() + value < 0) {
            log.info("outputOrigin.getBeijingInput() + value = {}", outputOrigin.getBeijingInput() + value);
            return Result.isError("退单后北京入库为负值");
        }
        if (outputOrigin.getBeijingStock() + value < 0) {
            log.info("outputOrigin.getBeijingStock() + value = {}", outputOrigin.getBeijingStock() + value);
            return Result.isError("退单后北京剩余为负值");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .beijing(origin.getBeijing() - value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .baoZhuang(outputOrigin.getBaoZhuang() + value)
                .beijingInput(outputOrigin.getBeijingInput() + value)
                .beijingStock(outputOrigin.getBeijingStock() + value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.BEIJING.getName(), value,
                ProduceEnum.BAO_ZHUANG.getName(), -value,
                //北京入库，北京库存
                OutputEnum.BEIJING_INPUT.getName(), value,
                comment);
    }

    /**
     * 出货
     * 进度：北京减少
     * 产值：北京剩余减少
     *
     * @param origin
     * @param value
     * @param comment
     * @return
     */
    private Result isOutput(Produce origin, int value, String comment) {
        if (origin.getBeijing() - value < 0) {
            log.info("origin.getBeijing() - value = {}", origin.getBeijing() - value);
            return Result.isError("北京库存不足");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getBeijingStock() - value < 0) {
            log.info("outputOrigin.getBeijingStock() - value = {}", outputOrigin.getBeijingStock() - value);
            return Result.isError("北京库存不足");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .beijing(origin.getBeijing() - value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .beijingStock(outputOrigin.getBeijingStock() - value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.BEIJING.getName(), -value,
                OutputEnum.BEIJING_STOCK.getName(), -value,
                "", 0,
                comment);
    }
}
