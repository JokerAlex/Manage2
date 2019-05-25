package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
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
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }

    /*
    private Result updateBaoZhuang(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        if (param.getProduceBaozhuang() == 0) {
            return Result.isError("更新值不能为 0 ");
        }

        //判断是否工厂出货
        if (flag == Constants.NOT_OUTPUT) {
            //进度：包装增加，油房减少
            //产值：油房增加
            if (param.getProduceBaozhuang() > produceSource.getProduceYoufang()) {
                return Result.isError("油房库存不足");
            } else if (param.getProduceBaozhuang() + produceSource.getProduceBaozhuang() < 0) {
                return Result.isError("退单量超过包装库存");
            } else if (outputSource.getOutputYoufang() + param.getProduceBaozhuang() < 0) {
                return Result.isError("退单后油房产值为负数");
            }
            //获取产品价格
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBaozhuang(param.getProduceBaozhuang() + produceSource.getProduceBaozhuang());
            update.setProduceBaozhuangComment(commentAppend(produceSource.getProduceBaozhuangComment(), "",
                    param.getProduceBaozhuang(), param.getProduceBaozhuangComment()));
            produceSource.setProduceYoufang(produceSource.getProduceYoufang() - param.getProduceBaozhuang());
            //油房产值
            outputSource.setOutputYoufang(outputSource.getOutputYoufang() + param.getProduceBaozhuang());
            outputSource.setOutputYoufangTotalPrice(outputSource.getOutputYoufang() * product.getProductPrice());
        } else {
            //工厂直接出货
            //进度：包装减少
            //产值：工厂出货增加，包装产值增加
            if (param.getProduceBaozhuang() > produceSource.getProduceBaozhuang()) {
                return Result.isError("包装库存不足");
            } else if (outputSource.getOutputBaozhuang() + param.getProduceBaozhuang() < 0) {
                return Result.isError("退单后包装产值为负数");
            } else if (outputSource.getOutputFactoryOutput() + param.getProduceBaozhuang() < 0) {
                return Result.isError("退单后工厂出货产值为负数");
            }
            //获取产品价格
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBaozhuang(produceSource.getProduceBaozhuang() - param.getProduceBaozhuang());
            update.setProduceBaozhuangComment(commentAppend(produceSource.getProduceBaozhuangComment(), "工厂出货",
                    param.getProduceBaozhuang(), param.getProduceBaozhuangComment()));
            //包装产值
            outputSource.setOutputBaozhuang(outputSource.getOutputBaozhuang() + param.getProduceBaozhuang());
            outputSource.setOutputBaozhuangTotalPrice(outputSource.getOutputBaozhuang() * product.getProductPrice());
            //工厂出货
            outputSource.setOutputFactoryOutput(outputSource.getOutputFactoryOutput() + param.getProduceBaozhuang());
            outputSource.setOutputFactoryOutputTotalPrice(outputSource.getOutputFactoryOutput() * product.getProductPrice());
        }
        return Result.isSuccess();
    }*/
}
