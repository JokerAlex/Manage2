package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
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
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }

    /*
    private Result updateBeiJing(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        if (param.getProduceBeijing() == 0) {
            return Result.isError("更新值不能为 0 ");
        }

        // flag 判断更新是否为北京出货
        if (flag == Constants.NOT_OUTPUT) {
            //进度：北京增加，包装减少
            //产值：包装增加
            if (param.getProduceBeijing() > produceSource.getProduceBaozhuang()) {
                return Result.isError("包装库存不足");
            } else if (param.getProduceBeijing() + produceSource.getProduceBeijing() < 0) {
                return Result.isError("退单量超过包装库存");
            } else if (outputSource.getOutputBaozhuang() + param.getProduceBeijing() < 0) {
                return Result.isError("退单后包装产值为负数");
            } else if (outputSource.getOutputBeijingInput() + param.getProduceBeijing() < 0) {
                return Result.isError("退单后北京入库为负数");
            } else if (outputSource.getOutputBeijingStock() + param.getProduceBeijing() < 0) {
                return Result.isError("退单后北京剩余为负数");
            }
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBeijing(param.getProduceBeijing() + produceSource.getProduceBeijing());
            produceSource.setProduceBaozhuang(produceSource.getProduceBaozhuang() - param.getProduceBeijing());
            update.setProduceBeijingComment(commentAppend(produceSource.getProduceBeijingComment(), "",
                    produceSource.getProduceBeijing(), param.getProduceBeijingComment()));
            //包装产值增加
            outputSource.setOutputBaozhuang(outputSource.getOutputBaozhuang() + param.getProduceBeijing());
            outputSource.setOutputBaozhuangTotalPrice(outputSource.getOutputBaozhuang() * product.getProductPrice());
            //北京入库
            outputSource.setOutputBeijingInput(outputSource.getOutputBeijingInput() + param.getProduceBeijing());
            outputSource.setOutputBeijingInputTotalPrice(outputSource.getOutputBeijingInput() * product.getProductPrice());
            //北京剩余增加
            outputSource.setOutputBeijingStock(outputSource.getOutputBeijingStock() + param.getProduceBeijing());
            outputSource.setOutputBeijingStockTotalPrice(outputSource.getOutputBeijingStock() * product.getProductPrice());
        } else {
            //出货
            //进度：北京减少
            //产值：北京剩余减少
            if (param.getProduceBeijing() > produceSource.getProduceBeijing()) {
                return Result.isError("北京库存不足");
            }
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBeijing(produceSource.getProduceBeijing() - param.getProduceBeijing());
            update.setProduceBeijingComment(commentAppend(produceSource.getProduceBeijingComment(), "出货",
                    produceSource.getProduceBeijing(), param.getProduceBeijingComment()));
            //北京剩余，减少
            outputSource.setOutputBeijingStock(outputSource.getOutputBeijingStock() - param.getProduceBeijing());
            outputSource.setOutputBeijingStockTotalPrice(outputSource.getOutputBeijingStock() * product.getProductPrice());
        }

        return Result.isSuccess();
    }*/
}
