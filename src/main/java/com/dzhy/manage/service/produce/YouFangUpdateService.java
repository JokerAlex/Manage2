package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
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

    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }

    /*
    private Result updateYouFang(Produce param, Produce produceSource, Produce update, Output outputSource) {
        //进度：油房增加，木工减少
        //产值：木工增加
        if (param.getProduceYoufang() == 0) {
            return Result.isError("更新值不能为 0 ");
        } else if (param.getProduceYoufang() > produceSource.getProduceMugong()) {
            return Result.isError("木工库存不足");
        } else if (param.getProduceYoufang() + produceSource.getProduceYoufang() < 0) {
            return Result.isError("退单超过油房库存");
        } else if (outputSource.getOutputMugong() + param.getProduceYoufang() < 0) {
            return Result.isError("退单后木工产值为负数");
        }
        //获取产品价格
        Product product = productMapper.findByProductId(produceSource.getProduceProductId());
        if (product == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
        }
        update.setProduceYoufang(param.getProduceYoufang() + produceSource.getProduceYoufang());
        update.setProduceYoufangComment(commentAppend(produceSource.getProduceYoufangComment(), "",
                param.getProduceYoufang(), param.getProduceYoufangComment()));
        produceSource.setProduceMugong(produceSource.getProduceMugong() - param.getProduceYoufang());
        //木工产值
        outputSource.setOutputMugong(outputSource.getOutputMugong() + param.getProduceYoufang());
        outputSource.setOutputMugongTotalPrice(outputSource.getOutputMugong() * product.getProductPrice());
        return Result.isSuccess();
    }*/
}
