package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.enums.ProduceEnum;
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

    /**
     * 本地合同自增自减
     *
     * @param origin
     * @param value
     * @param comment
     * @param flag
     * @return
     */
    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        if (origin.getBendiHetong() + value < 0) {
            log.info("origin.getBendiHetong() + value = {}", origin.getBendiHetong() + value);
            return Result.isError("退单量超过已有本地合同量");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .bendiHetong(origin.getBendiHetong() + value)
                .build();
        return getResult(origin, update, ProduceEnum.BENDI_HETONG, value, comment);
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
