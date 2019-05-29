package com.dzhy.manage.service;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.dao.ProduceMapper;
import com.dzhy.manage.dao.ProductMapper;
import com.dzhy.manage.dao.ProductSukMapper;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.entity.ProductSuk;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.utils.CommonUtil;
import com.dzhy.manage.utils.ExcelUtils;
import com.dzhy.manage.utils.FileUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ProductService
 * @Description 产品信息 service
 * @Author alex
 * @Date 2019-05-17
 **/
@Service
@Slf4j
public class ProductService {
    @Value("${manage.img.filePath}")
    private String filePath;

    @Value("${manage.fileType}")
    private String[] fileTypes;

    private final ProductMapper productMapper;
    private final ProductSukMapper productSukMapper;
    private final ProduceMapper produceMapper;

    @Autowired
    public ProductService(ProductMapper productMapper,
                          ProductSukMapper productSukMapper,
                          ProduceMapper produceMapper) {
        this.productMapper = productMapper;
        this.productSukMapper = productSukMapper;
        this.produceMapper = produceMapper;
    }

    public Result checkProductName(String productName) throws GeneralException {
        if (StringUtils.isBlank(productName)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage() + ":" + productName);
        }

        int count = productMapper.existsByProductName(productName);
        if (count > 0) {
            return Result.isError(ResultEnum.UNUSABLE_NAME.getMessage() + " 名称:" + productName);
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result insertProduct(Product product, MultipartFile multipartFile) throws GeneralException {
        if (product == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        Result r = this.checkProductName(product.getProductName());
        if (!r.isOk()) {
            return r;
        }
        Product insert = Product.builder()
                .productName(product.getProductName())
                .categoryId(product.getCategoryId())
                .comments(product.getComments())
                .build();
        try {
            int count = productMapper.insertSelective(insert);
            int productId = insert.getProductId();
            log.info("add product success productId:{}, count:{}", productId, count);
            //存储sku信息
            /*productSukList = productSukList.stream()
                    .map(sku -> ProductSuk.builder()
                            .productId(productId)
                            .sukName(sku.getSukName())
                            .price(sku.getPrice())
                            .build())
                    .collect(Collectors.toList());
            count = productSukMapper.insertBatch(productSukList);
            log.info("add productSku, productSukList:{}, count:{}", count, productSukList.size());*/
            if (multipartFile != null) {
                this.updateHeadImg(productId, multipartFile);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    //todo sku价格相关信息
    @Transactional(rollbackFor = GeneralException.class)
    public Result importProduct(MultipartFile multipartFile) throws GeneralException, IOException {
        if (multipartFile == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        String fileName = multipartFile.getOriginalFilename();
        log.info("fileName = {}", fileName);
        //判断文件类型
        //读取文件内容并存储
        assert fileName != null;
        if (!fileName.substring(fileName.lastIndexOf(ExcelUtils.POINT)).equals(ExcelUtils.EXCEL_2003L)
                && !fileName.substring(fileName.lastIndexOf(ExcelUtils.POINT)).equals(ExcelUtils.EXCEL_2007U)) {
            return Result.isError(ResultEnum.ILLEGAL_FILE_TYPE.getMessage());
        }
        //excel文件读取，写入数据库
        List<Map<String, String>> readResult = ExcelUtils.readToMapList(multipartFile.getInputStream());
        List<Product> productList = readResult.stream()
                .map(row -> {
                    if (productMapper.existsByProductName(row.get(Constants.PRODUCT_NAME)) != 0) {
                        throw new GeneralException(ResultEnum.IS_EXIST.getMessage()
                                + "-名称:" + row.get(Constants.PRODUCT_NAME));
                    }

                    return Product.builder()
                            .productName(row.get(Constants.PRODUCT_NAME))
                            .categoryId(Integer.valueOf(row.get(Constants.CATEGORY_ID)))
                            .headImg(row.get(Constants.HEAD_IMG))
                            .img(row.get(Constants.IMG))
                            .comments(row.get(Constants.COMMENT))
                            .build();
                })
                .collect(Collectors.toList());
        try {
            int count = productMapper.insertBatch(productList);
            log.info("import product count:{}", count);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.IMPORT_ERROR.getMessage());
        }
        return Result.isSuccess();
    }


    @Transactional(rollbackFor = GeneralException.class)
    public Result updateProduct(Product product) throws GeneralException {
        if (product == null || product.getProductId() == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Product update = Product.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategoryId())
                .comments(product.getComments())
                .build();
        try {
            int count = productMapper.updateByPrimaryKeySelective(update);
            log.info("update product productId:{}, count:{}", update.getProductId(), count);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result updateHeadImg(Integer productId, MultipartFile multipartFile) throws GeneralException {
        if (productId == null || multipartFile == null || multipartFile.isEmpty()) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage());
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!isValid(fileType, fileTypes)) {
            return Result.isError(originalFilename + "图片格式错误");
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + fileType;
        FileUtil.upload(multipartFile, filePath, fileName);
        Product update = Product.builder()
                .productId(product.getProductId())
                .headImg(fileName)
                .build();
        try {
            int count = productMapper.updateByPrimaryKeySelective(update);
            //删除文件
            FileUtil.del(Collections.singletonList(fileName), filePath);
            log.info("upload head pictures success productId:{}, count:{}", productId, count);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result updatePictures(Integer productId, List<MultipartFile> multipartFiles) throws GeneralException{
        if (productId == null || CollectionUtils.isEmpty(multipartFiles)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage());
        }
        List<String> pictureNameList = Lists.newArrayList();
        Map<String, MultipartFile> map = new LinkedHashMap<>(multipartFiles.size());
        //图片重命名
        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.isEmpty() || multipartFile.getOriginalFilename() == null) {
                return Result.isError("图片错误");
            }
            String originalFilename = multipartFile.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!isValid(fileType, fileTypes)) {
                return Result.isError(originalFilename + "图片格式错误");
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + fileType;
            map.put(fileName, multipartFile);
            pictureNameList.add(fileName);
        }
        FileUtil.upload(map, filePath);
        //产品保存图片信息
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(product.getImg())) {
            sb.append(product.getImg()).append(",");
        }
        sb.append(StringUtils.join(pictureNameList, ","));
        Product update = Product.builder()
                .productId(product.getProductId())
                .img(sb.toString())
                .build();
        try {
            int count = productMapper.updateByPrimaryKeySelective(update);
            log.info("upload pictures success productId:{}, count:{}", productId, count);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }


    @Transactional(rollbackFor = GeneralException.class)
    public Result deletePictures(Integer productId, List<String> fileNames) throws GeneralException{
        if (productId == null || CollectionUtils.isEmpty(fileNames)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage());
        }
        if (StringUtils.isBlank(product.getImg())) {
            return Result.isError("该产品没有图片");
        }
        List<String> pictureList = Lists.newArrayList(product.getImg().split(","));
        boolean delImgStr = pictureList.removeAll(fileNames);
        //产品图片信息更新
        Product update = Product.builder()
                .productId(product.getProductId())
                .img(StringUtils.join(pictureList, ","))
                .build();
        try {
            int count = productMapper.updateByPrimaryKeySelective(update);
            //删除文件
            FileUtil.del(fileNames, filePath);
            log.info("delete pictures success productId:{}, delImgStr:{}, count:{}", productId, delImgStr, count);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result deleteProductBatch(List<Integer> productIds) throws GeneralException {
        if (CollectionUtils.isEmpty(productIds)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        try {
            //获取所有图片名称
            List<Product> productList = productMapper.selectByIds(productIds);
            List<String> pictures = Lists.newArrayList();
            int date = CommonUtil.getDateToIntNow();
            for (Product product : productList) {
                if (null != produceMapper.selectByIdAndDate(product.getProductId(), date)) {
                    return Result.isError("该产品在进度表中仍有使用记录，不能删除");
                }

                if (StringUtils.isNotBlank(product.getHeadImg())) {
                    pictures.add(product.getHeadImg());
                }
                if (StringUtils.isNotBlank(product.getImg())) {
                    pictures.addAll(Lists.newArrayList(product.getImg().split(",")));
                }
            }
            int count = productMapper.deleteBatch(productIds);
            log.info("delete product batch, count:{}, productIds:{}", count, productIds);
            if (count > 0 && CollectionUtils.isNotEmpty(pictures)) {
                FileUtil.del(pictures, filePath);
                log.info("delete product batch pictures : {}", pictures);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }


    public Result listProduct(int pageNum, int pageSize, String productName, Integer categoryId) throws GeneralException {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectByConditions(productName, categoryId);
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        return Result.isSuccess(pageInfo);
    }

    public Result getDetails(Integer productId) throws GeneralException {
        if (productId == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage() + " ID:" + productId);
        }
        return Result.isSuccess(product);
    }

    private boolean isValid(String fileType, String[] allowTypes) {
        if (StringUtils.isBlank(fileType)) {
            return false;
        }
        for (String type : allowTypes) {
            if (type.equals(fileType)) {
                return true;
            }
        }
        return false;
    }
}
