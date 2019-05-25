package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
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

    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        /*
    private Result updateMuGong(Produce param, Produce produceSource, Produce update, Output outputSource) {
        //进度：木工增加，下单减少
        //产值：下单增加
        if (param.getProduceMugong() == 0) {
            return Result.isError("更新值不能为 0 ");
        } else if (param.getProduceMugong() > produceSource.getProduceXiadan()) {
            return Result.isError("下单库存不足");
        } else if (param.getProduceMugong() + produceSource.getProduceMugong() < 0) {
            return Result.isError("退单超过木工库存");
        } else if (outputSource.getOutputXiadan() + param.getProduceMugong() < 0) {
            return Result.isError("退单后下单产值为负数");
        }
        update.setProduceMugong(param.getProduceMugong() + produceSource.getProduceMugong());
        update.setProduceMugongComment(commentAppend(produceSource.getProduceMugongComment(), "",
                param.getProduceMugong(), param.getProduceMugongComment()));
        produceSource.setProduceXiadan(produceSource.getProduceXiadan() - param.getProduceMugong());
        outputSource.setOutputXiadan(outputSource.getOutputXiadan() + param.getProduceMugong());
        return Result.isSuccess();
    }*/
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }
}
