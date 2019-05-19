package com.dzhy.manage.dao;

import com.dzhy.manage.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer productId);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer productId);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    int existsByProductName(String productName);

    int insertBatch(List<Product> list);

    List<Product> selectByConditions(@Param("productName") String productName, @Param("categoryId") Integer categoryId);

    List<Product> selectByIds(@Param("list") List<Integer> productIds);

    int deleteBatch(@Param("list") List<Integer> productIds);
}