package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.enums.OutputEnum;
import com.dzhy.manage.enums.ProduceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName YouFangUpdateService
 * @Description 油房更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class YouFangUpdateService extends AbstractUpdateService {

    /**
     * 进度：油房增加，木工减少
     * 产值：木工增加
     *
     * @param origin
     * @param value
     * @param comment
     * @param flag
     * @return
     */
    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        if (origin.getMuGong() - value < 0) {
            log.info("origin.getMuGong() - value = {}", origin.getMuGong() - value);
            return Result.isError("木工库存不足");
        }
        if (origin.getYouFang() + value < 0) {
            log.info("origin.getYouFang() + value = {}", origin.getYouFang() + value);
            return Result.isError("退单超过油房库存");
        }
        Output outputOrigin = this.getOutput(origin);
        if (outputOrigin.getMuGong() + value < 0) {
            return Result.isError("退单后木工产值为负数");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .muGong(origin.getMuGong() - value)
                .youFang(origin.getYouFang() + value)
                .build();
        Output outputUpdate = Output.builder()
                .outputId(outputOrigin.getOutputId())
                .muGong(outputOrigin.getMuGong() + value)
                .build();
        return getResult(origin, update, outputUpdate,
                ProduceEnum.YOU_FANG.getName(), value,
                ProduceEnum.MU_GONG.getName(), -value,
                OutputEnum.MU_GONG.getName(), value,
                comment);
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .youFang(value)
                .build();
        return getResult(origin, update, ProduceEnum.YOU_FANG, value, comment);
    }
}
