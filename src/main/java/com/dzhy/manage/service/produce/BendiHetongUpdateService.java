package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName BendiHetongUpdateService
 * @Description 本地合同更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class BendiHetongUpdateService extends AbstractUpdateService {

    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }

    /*
    private Result updateBenDiHeTong(Produce param, Produce produceSource, Produce update) {
        //进度：本地合同自己增加，减少
        //产值：没有变化
        if (param.getProduceBendihetong() == 0) {
            return Result.isError("更新值不能为 0 ");
        } else if (param.getProduceBendihetong() + produceSource.getProduceBendihetong() < 0) {
            return Result.isError("退单量超过已有本地合同量");
        }
        update.setProduceBendihetong(param.getProduceBendihetong() + produceSource.getProduceBendihetong());
        update.setProduceBendihetongComment(commentAppend(produceSource.getProduceBendihetongComment(), "",
                param.getProduceBendihetong(), param.getProduceBendihetongComment()));
        return Result.isSuccess();
    }*/
}
