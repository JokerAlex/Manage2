package com.dzhy.manage.dao;

import com.dzhy.manage.entity.ProductSuk;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSukMapper {
    int deleteByPrimaryKey(Integer skuId);

    int insert(ProductSuk record);

    int insertSelective(ProductSuk record);

    ProductSuk selectByPrimaryKey(Integer skuId);

    int updateByPrimaryKeySelective(ProductSuk record);

    int updateByPrimaryKey(ProductSuk record);

    int insertBatch(List<ProductSuk> list);

    int deleteBatch(List<Integer> list);

    List<ProductSuk> selectByProductId(Integer productId);
}