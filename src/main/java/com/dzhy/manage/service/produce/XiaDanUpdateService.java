package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.enums.ProduceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName XiaDanUpdateService
 * @Description 下单更新
 * @Author alex
 * @Date 2019-05-25
 **/
@Service
@Slf4j
public class XiaDanUpdateService extends AbstractUpdateService {

    /**
     * 进度：下单增加
     *
     * @param origin
     * @param value
     * @param comment
     * @param flag
     * @return
     */
    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        if (origin.getXiaDan() + value < 0) {
            log.info("更新后，下单值为负数:{}", origin.getXiaDan() + value);
            return Result.isError("更新后，下单值为负数");
        }
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .xiaDan(origin.getXiaDan() + value)
                .build();
        return getResult(origin, update, ProduceEnum.XIA_DAN, value, comment);
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        Produce update = Produce.builder()
                .produceId(origin.getProduceId())
                .xiaDan(value)
                .build();
        return getResult(origin, update, ProduceEnum.XIA_DAN, value, comment);
    }
}
