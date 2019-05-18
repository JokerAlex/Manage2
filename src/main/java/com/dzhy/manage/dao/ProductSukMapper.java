package com.dzhy.manage.dao;

import com.dzhy.manage.entity.ProductSuk;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSukMapper {
    int deleteByPrimaryKey(Integer skuId);

    int insert(ProductSuk record);

    int insertSelective(ProductSuk record);

    ProductSuk selectByPrimaryKey(Integer skuId);

    int updateByPrimaryKeySelective(ProductSuk record);

    int updateByPrimaryKey(ProductSuk record);
}