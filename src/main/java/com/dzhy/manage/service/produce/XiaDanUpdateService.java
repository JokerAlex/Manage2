package com.dzhy.manage.service.produce;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.dao.ProduceMapper;
import com.dzhy.manage.dao.ProduceRecordMapper;
import com.dzhy.manage.entity.Produce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ProduceMapper produceMapper;
    private final ProduceRecordMapper produceRecordMapper;

    @Autowired
    public XiaDanUpdateService(ProduceMapper produceMapper, ProduceRecordMapper produceRecordMapper) {
        this.produceMapper = produceMapper;
        this.produceRecordMapper = produceRecordMapper;
    }

    @Override
    public Result update(Produce origin, int value, String comment, int flag) {
        /*private Result updateXiaDan(Produce param, Produce produceSource, Produce update) {
            //下单增加
            if (param.getProduceXiadan() == 0) {
                return Result.isError("更新值不能为 0 ");
            }
            if (param.getProduceXiadan() + produceSource.getProduceXiadan() < 0) {
                return Result.isError("更新后，下单值为负数");
            }
            update.setProduceXiadan(param.getProduceXiadan() + produceSource.getProduceXiadan());
            update.setProduceXiadanComment(commentAppend(produceSource.getProduceXiadanComment(), "",
                    param.getProduceXiadan(), param.getProduceXiadanComment()));
            return Result.isSuccess();
        }*/
        return null;
    }

    @Override
    public Result fix(Produce origin, int value, String comment) {
        return null;
    }
}
