package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName WaidiHetongUpdateService
 * @Description 外地合同更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class WaidiHetongUpdateService extends AbstractUpdateService {

    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }

    /*
    private Result updateWaiDiHeTong(Produce param, Produce produceSource, Produce update) {
        //进度：外地合同自己增加，减少
        //产值：没有变化
        if (param.getProduceWaidihetong() == 0) {
            return Result.isError("更新值不能为 0 ");
        } else if (param.getProduceWaidihetong() + produceSource.getProduceWaidihetong() < 0) {
            return Result.isError("退单量超过已有外地合同量");
        }
        update.setProduceWaidihetong(param.getProduceWaidihetong() + produceSource.getProduceWaidihetong());
        update.setProduceWaidihetongComment(commentAppend(produceSource.getProduceWaidihetongComment(), "",
                param.getProduceWaidihetong(), param.getProduceWaidihetongComment()));
        return Result.isSuccess();
    }*/
}
