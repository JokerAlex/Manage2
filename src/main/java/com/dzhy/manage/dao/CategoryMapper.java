package com.dzhy.manage.dao;

import com.dzhy.manage.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer categoryId);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer categoryId);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    Category selectByCategoryName(String categoryName);

    int deleteBatch(@Param("list") List<Integer> categoryIds);

    List<Category> selectAll();
}