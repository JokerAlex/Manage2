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
 * @ClassName TeDingUpdateService
 * @Description 特定更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class TeDingUpdateService extends AbstractUpdateService {

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
                .teDing(value)
                .build();
        return getResult(origin, update, ProduceEnum.TE_DING, value, comment);
    }

    /**
     * 进度：特定增加，油房减少
     * 产值：油房增加
     *
     * @param origin
     * @param value
     * @param comment
     * @return
     */
    private Result isNotOutput(Produce origin, int value, String comment) {
        if (origin.getYouFang() - value < 0) {
            log.info("origin.getYouFang() - value = {}", origin.getYouFang() - value);
            return Result.isError("油房库存不足");
        }
        if (origin.getTeDing() + value < 0) {
            return Result.isError("退单量超过特定库存");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getYouFang() + value < 0) {
            return Result.isError("退单后油房产值为负数");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .youFang(origin.getYouFang() - value)
                .teDing(origin.getTeDing() + value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .youFang(outputOrigin.getYouFang() + value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.TE_DING.getName(), value,
                ProduceEnum.YOU_FANG.getName(), -value,
                OutputEnum.YOU_FANG.getName(), value,
                comment);
    }

    /**
     * 工厂直接出货
     * 进度：特定减少
     * 产值：特定工厂出货增加，特定产值增加
     *
     * @param origin
     * @param value
     * @param comment
     * @return
     */
    private Result isOutput(Produce origin, int value, String comment) {
        if (origin.getTeDing() - value < 0) {
            log.info("origin.getTeDing() - value = {}", origin.getTeDing() - value);
            return Result.isError("特定库存不足");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getTeDing() + value < 0) {
            log.info("outputOrigin.getTeDing() + value = {}", outputOrigin.getTeDing() + value);
            return Result.isError("退单后特定产值为负数");
        }
        if (outputOrigin.getTedingFactoryOutput() + value < 0) {
            log.info("outputOrigin.getTedingFactoryOutput() + value = {}", outputOrigin.getTedingFactoryOutput() + value);
            return Result.isError("退单后特定工厂出货产值为负数");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .teDing(origin.getTeDing() - value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .tedingFactoryOutput(outputOrigin.getTeDing() + value)
                .tedingFactoryOutput(outputOrigin.getTedingFactoryOutput() + value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.TE_DING.getName(), -value,
                OutputEnum.TE_DING.getName(), value,
                OutputEnum.TEDING_FACTORY_OUTPUT.getName(), value,
                comment);
    }
}
