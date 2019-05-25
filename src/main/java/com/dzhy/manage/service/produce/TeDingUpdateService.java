package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
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
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }

    /*
    private Result updateTeDing(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        if (param.getProduceTeding() == 0) {
            return Result.isError("更新值不能为 0 ");
        }
        //判断是否工厂出货
        if (flag == Constants.NOT_OUTPUT) {
            //进度：特定增加，油房减少
            //产值：油房增加
            if (param.getProduceTeding() > produceSource.getProduceYoufang()) {
                return Result.isError("油房库存不足");
            } else if (param.getProduceTeding() + produceSource.getProduceTeding() < 0) {
                return Result.isError("退单量超过特定库存");
            } else if (outputSource.getOutputYoufang() + param.getProduceTeding() < 0) {
                return Result.isError("退单后油房产值为负数");
            }
            //获取产品价格
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceTeding(param.getProduceTeding() + produceSource.getProduceTeding());
            update.setProduceTedingComment(commentAppend(produceSource.getProduceTedingComment(), "",
                    param.getProduceTeding(), param.getProduceTedingComment()));
            produceSource.setProduceYoufang(produceSource.getProduceYoufang() - param.getProduceTeding());
            //油房产值
            outputSource.setOutputYoufang(outputSource.getOutputYoufang() + param.getProduceTeding());
            outputSource.setOutputYoufangTotalPrice(outputSource.getOutputYoufang() * product.getProductPrice());
        } else {
            //工厂直接出货
            //进度：特定减少
            //产值：特定工厂出货增加，特定产值增加
            if (param.getProduceTeding() > produceSource.getProduceTeding()) {
                return Result.isError("特定库存不足");
            } else if (outputSource.getOutputTeding() + param.getProduceTeding() < 0) {
                return Result.isError("退单后特定产值为负数");
            } else if (outputSource.getOutputTedingFactoryOutput() + param.getProduceTeding() < 0) {
                return Result.isError("退单后特定工厂出货产值为负数");
            }
            //获取产品价格
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceTeding(produceSource.getProduceTeding() - param.getProduceTeding());
            update.setProduceTedingComment(commentAppend(produceSource.getProduceTedingComment(), "工厂直接出货",
                    param.getProduceTeding(), param.getProduceTedingComment()));
            //特定产值
            outputSource.setOutputTeding(outputSource.getOutputTeding() + param.getProduceTeding());
            outputSource.setOutputTedingTotalPrice(outputSource.getOutputTeding() * product.getProductPrice());
            //特定工厂出货
            outputSource.setOutputTedingFactoryOutput(outputSource.getOutputTedingFactoryOutput() + param.getProduceTeding());
            outputSource.setOutputTedingFactoryOutputTotalPrice(outputSource.getOutputTedingFactoryOutput() * product.getProductPrice());
        }

        return Result.isSuccess();
    }*/
}
