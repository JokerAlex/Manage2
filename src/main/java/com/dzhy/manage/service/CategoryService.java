package com.dzhy.manage.service;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.dao.CategoryMapper;
import com.dzhy.manage.entity.Category;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName CategoryService
 * @Description 产品分类 service
 * @Author alex
 * @Date 2019-05-17
 **/
@Service
@Slf4j
public class CategoryService {

    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }
    
    public Category checkCategoryName(String categoryName) throws GeneralException {
        if (StringUtils.isBlank(categoryName)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        return categoryMapper.selectByCategoryName(categoryName);

    }


    @Transactional(rollbackFor = GeneralException.class)
    public Result addCategory(Category category) throws GeneralException {
        if (category == null || StringUtils.isBlank(category.getCategoryName())) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Category categorySource = this.checkCategoryName(category.getCategoryName());
        if (categorySource != null) {
            return Result.isError("该分类已存在");
        }
        Category insert = Category.builder()
                .categoryName(category.getCategoryName())
                .parentId(category.getParentId())
                .build();
        try {
            int count = categoryMapper.insertSelective(insert);
            log.info("add category success count:{} categoryId:{}", count, insert.getCategoryId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return Result.isSuccess();
    }


    @Transactional(rollbackFor = GeneralException.class)
    public Result updateCategory(Category category) throws GeneralException {
        if (category == null || category.getCategoryId() == null || StringUtils.isBlank(category.getCategoryName())) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Category categorySource = this.checkCategoryName(category.getCategoryName());
        if (categorySource != null) {
            return Result.isError("该分类已存在");
        }
        Category update = Category.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .parentId(category.getParentId())
                .build();
        try {
            int count = categoryMapper.updateByPrimaryKeySelective(update);
            log.info("update category success count:{} categoryId:{}", count, update.getCategoryId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result deleteCategoryBatch(List<Integer> categoryIds) throws GeneralException {
        if (CollectionUtils.isEmpty(categoryIds)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        try {
            int count = categoryMapper.deleteBatch(categoryIds);
            log.info("count:{} categoryIds : {}", count, categoryIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }


    public Result listCategory() {
        List<Category> categoryList = categoryMapper.selectAll();
        return Result.isSuccess(categoryList);
    }
}
