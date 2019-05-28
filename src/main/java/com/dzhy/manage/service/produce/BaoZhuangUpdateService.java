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
 * @ClassName BaoZhuangUpdateService
 * @Description 包装更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class BaoZhuangUpdateService extends AbstractUpdateService {

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
                .baoZhuang(value)
                .build();
        return getResult(origin, update, ProduceEnum.BAO_ZHUANG, value, comment);
    }

    /**
     * 进度：包装增加，油房减少
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
        if (origin.getBaoZhuang() + value < 0) {
            log.info("origin.getBaoZhuang() + value = {}", origin.getBaoZhuang() + value);
            return Result.isError("退单超过包装库存");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getYouFang() + value < 0) {
            return Result.isError("退单后油房产值为负数");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .youFang(origin.getYouFang() - value)
                .baoZhuang(origin.getBaoZhuang() + value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .youFang(outputOrigin.getYouFang() + value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.BAO_ZHUANG.getName(), value,
                ProduceEnum.YOU_FANG.getName(), -value,
                OutputEnum.YOU_FANG.getName(), value,
                comment);
    }

    /**
     * 工厂直接出货
     * 进度：包装减少
     * 产值：工厂出货增加，包装产值增加
     *
     * @param origin
     * @param value
     * @param comment
     * @return
     */
    private Result isOutput(Produce origin, int value, String comment) {
        if (origin.getBaoZhuang() - value < 0) {
            log.info("origin.getBaoZhuang() - value = {}", origin.getBaoZhuang() - value);
            return Result.isError("包装库存不足");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getBaoZhuang() + value < 0) {
            log.info("outputOrigin.getBaoZhuang() + value = {}", outputOrigin.getBaoZhuang() + value);
            return Result.isError("退单后包装产值为负数");
        }
        if (outputOrigin.getFactoryOutput() + value < 0) {
            log.info("outputOrigin.getFactoryOutput() + value = {}", outputOrigin.getFactoryOutput() + value);
            return Result.isError("退单后工厂出货产值为负数");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .baoZhuang(origin.getBaoZhuang() - value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .baoZhuang(outputOrigin.getBaoZhuang() + value)
                .factoryOutput(outputOrigin.getFactoryOutput() + value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.BAO_ZHUANG.getName(), -value,
                OutputEnum.BAO_ZHUANG.getName(), value,
                OutputEnum.FACTORY_OUTPUT.getName(), value,
                comment);
    }
}
