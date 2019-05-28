package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.enums.OutputEnum;
import com.dzhy.manage.enums.ProduceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName MuGongUpdateService
 * @Description 木工更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class MuGongUpdateService extends AbstractUpdateService {

    /**
     * 进度：木工增加，下单减少
     * 产值：下单增加
     *
     * @param origin
     * @param value
     * @param comment
     * @param flag
     * @return
     */
    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        if (origin.getXiaDan() - value < 0) {
            log.info("origin.getXiaDan() - value = {}", origin.getXiaDan() - value);
            return Result.isError("下单库存不足");
        }
        if (origin.getMuGong() + value < 0) {
            log.info("origin.getXiaDan() + value = {}", origin.getXiaDan() + value);
            return Result.isError("退单超过木工库存");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getXiaDan() + value < 0) {
            return Result.isError("退单后下单产值为负数");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .xiaDan(origin.getXiaDan() - value)
                .muGong(origin.getMuGong() + value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .xiaDan(outputOrigin.getXiaDan() + value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.MU_GONG.getName(), value,
                ProduceEnum.XIA_DAN.getName(), -value,
                OutputEnum.XIA_DAN.getName(), value,
                comment);
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .muGong(value)
                .build();
        return getResult(origin, update, ProduceEnum.MU_GONG, value, comment);
    }
}
