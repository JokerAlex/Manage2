package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.enums.ProduceEnum;
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

    /**
     * 外地合同自增自减
     *
     * @param origin
     * @param value
     * @param comment
     * @param flag
     * @return
     */
    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        if (origin.getWaidiHetong() + value < 0) {
            log.info("origin.getWaidiHetong() + value = {}", origin.getWaidiHetong() + value);
            return Result.isError("退单量超过已有外地合同量");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .waidiHetong(origin.getWaidiHetong() + value)
                .build();
        return getResult(origin, update, ProduceEnum.WAIDI_HETONG, value, comment);
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .bendiHetong(value)
                .build();
        return getResult(origin, update, ProduceEnum.BENDI_HETONG, value, comment);
    }
}
