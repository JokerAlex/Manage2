package com.dzhy.manage.service;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.dao.OutputMapper;
import com.dzhy.manage.dao.ProduceMapper;
import com.dzhy.manage.dao.ProductSukMapper;
import com.dzhy.manage.entity.ProductSuk;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ProductSkuService
 * @Description TODO
 * @Author alex
 * @Date 2019-05-19
 **/
@Service
@Slf4j
public class ProductSkuService {

    private final ProductSukMapper productSukMapper;
    private final ProduceMapper produceMapper;
    private final OutputMapper outputMapper;

    @Autowired
    public ProductSkuService(ProductSukMapper productSukMapper,
                             ProduceMapper produceMapper,
                             OutputMapper outputMapper) {
        this.productSukMapper = productSukMapper;
        this.produceMapper = produceMapper;
        this.outputMapper = outputMapper;
    }


    @Transactional(rollbackFor = GeneralException.class)
    public Result insertProductSuk(List<ProductSuk> productSukList) throws GeneralException {
        if (CollectionUtils.isEmpty(productSukList)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        productSukList = productSukList.stream()
                .map(suk -> ProductSuk.builder()
                        .productId(suk.getProductId())
                        .skuName(suk.getSkuName())
                        .price(suk.getPrice())
                        .build())
                .collect(Collectors.toList());
        try {
            int count = productSukMapper.insertBatch(productSukList);
            log.info("insert productSuk count:{}, productSukListSize:{}", count, productSukList.size());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result updateProductSuk(ProductSuk productSuk) throws GeneralException {
        if (productSuk == null || productSuk.getSkuId() == null || productSuk.getProductId() == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        ProductSuk source = productSukMapper.selectByPrimaryKey(productSuk.getSkuId());
        boolean price = source.getPrice().equals(productSuk.getPrice());
        ProductSuk update = ProductSuk.builder()
                .skuId(productSuk.getSkuId())
                .productId(productSuk.getProductId())
                .skuName(productSuk.getSkuName())
                .price(productSuk.getPrice())
                .build();
        try {
            int count = productSukMapper.updateByPrimaryKeySelective(update);
            log.info("update productSuk productId:{}, count:{}", update.getProductId(), count);
            if (!price) {
                //价格修改
                count = produceMapper.updatePrice(update.getSkuId(), update.getPrice());
                log.info("update produce price, count:{}, price:{}", count, update.getPrice());
                count = outputMapper.updatePrice(update.getSkuId(), update.getPrice());
                log.info("update output price, count:{}, price:{}", count, update.getPrice());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result deleteProductSuk(List<Integer> sukIds) throws GeneralException {
        if (CollectionUtils.isEmpty(sukIds)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        try {
            int count = productSukMapper.deleteBatch(sukIds);
            log.info("delete productSuk batch, count:{}", count);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    public Result listProductSuk(Integer productId) {
        if (productId == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        List<ProductSuk> sukList = productSukMapper.selectByProductId(productId);
        return Result.isSuccess(sukList);
    }
}
