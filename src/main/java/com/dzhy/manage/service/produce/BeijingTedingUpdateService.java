package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
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
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }

    /*
    private Result updateBeiJingTeDing(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        if (param.getProduceBeijingteding() == 0) {
            return Result.isError("更新值不能为 0 ");
        }
        //判断是否为北京特定出货
        if (flag == Constants.NOT_OUTPUT) {
            //进度：北京特定增加，特定减少
            //产值：特定增加
            if (param.getProduceBeijingteding() > produceSource.getProduceTeding()) {
                return Result.isError("特定库存不足");
            } else if (param.getProduceBeijingteding() + produceSource.getProduceBeijingteding() < 0) {
                return Result.isError("退单量超过北京特定库存");
            } else if (outputSource.getOutputBeijingtedingInput() + param.getProduceBeijingteding() < 0) {
                return Result.isError("退单后北京特定入库为负数");
            } else if (outputSource.getOutputBeijingtedingStock() + param.getProduceBeijingteding() < 0) {
                return Result.isError("退单后北京特定剩余为负数");
            }
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBeijingteding(param.getProduceBeijingteding() + produceSource.getProduceBeijingteding());
            produceSource.setProduceTeding(produceSource.getProduceTeding() - param.getProduceBeijingteding());
            update.setProduceBeijingtedingComment(commentAppend(produceSource.getProduceBeijingtedingComment(), "",
                    produceSource.getProduceBeijingteding(), param.getProduceBeijingtedingComment()));
            //特定产值
            outputSource.setOutputTeding(outputSource.getOutputTeding() + param.getProduceBeijingteding());
            outputSource.setOutputTedingTotalPrice(outputSource.getOutputTeding() * product.getProductPrice());
            //北京特定入库
            outputSource.setOutputBeijingtedingInput(outputSource.getOutputBeijingtedingInput() + param.getProduceBeijingteding());
            outputSource.setOutputBeijingtedingInputTotalPrice(outputSource.getOutputBeijingtedingInput() * product.getProductPrice());
            //北京特定剩余增加
            outputSource.setOutputBeijingtedingStock(outputSource.getOutputBeijingtedingStock() + param.getProduceBeijingteding());
            outputSource.setOutputBeijingtedingStockTotalPrice(outputSource.getOutputBeijingtedingStock() * product.getProductPrice());
        } else {
            //出货
            //进度：北京特定减少./pa
            //产值：北京特定剩余减少
            if (param.getProduceBeijingteding() > produceSource.getProduceBeijingteding()) {
                return Result.isError("北京特定库存不足");
            }
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBeijingteding(produceSource.getProduceBeijingteding() - param.getProduceBeijingteding());
            update.setProduceBeijingtedingComment(commentAppend(produceSource.getProduceBeijingtedingComment(), "出货",
                    produceSource.getProduceBeijingteding(), param.getProduceBeijingtedingComment()));
            //北京剩余，减少
            outputSource.setOutputBeijingtedingStock(outputSource.getOutputBeijingtedingStock() - param.getProduceBeijingteding());
            outputSource.setOutputBeijingtedingStockTotalPrice(outputSource.getOutputBeijingtedingStock() * product.getProductPrice());
        }

        return Result.isSuccess();
    }*/
}
