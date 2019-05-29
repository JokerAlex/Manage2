package com.dzhy.manage.controller;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.service.ProductService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName ProductController
 * @Description 产品管理 controller
 * @Author alex
 * @Date 2019-05-29
 **/

@RestController
@RequestMapping("/product")
@Api(value = "产品", description = "产品管理")
public class ProductController {

    private final ProductService iProductService;

    @Autowired
    public ProductController(ProductService iProductService) {
        this.iProductService = iProductService;
    }

    @ApiOperation(value = "检查产品名称", notes = "添加新的产品前检查产品名称是否在数据库中已有记录")
    @ApiImplicitParam(name = "productName", value = "产品名称", required = true, dataTypeClass = String.class)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @GetMapping("/check")
    public Result checkProductName(@RequestParam(value = "productName") String productName) {
        return iProductService.checkProductName(productName);
    }

    @ApiOperation(value = "添加产品", notes = "添加新的产品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "产品名称", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "categoryId", value = "产品类别", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "comment", value = "产品备注", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "multipartFile", value = "产品大图", dataTypeClass = MultipartFile.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PostMapping()
    public Result addProduct(@RequestParam(value = "productName") String productName,
                             @RequestParam(value = "categoryId") Integer categoryId,
                             @RequestParam(value = "comment") String comment,
                             @RequestParam(value = "multipartFile") MultipartFile multipartFile) {
        Product product = Product.builder()
                .productName(productName)
                .categoryId(categoryId)
                .comments(comment)
                .build();
        return iProductService.insertProduct(product, multipartFile);
    }

    @ApiOperation(value = "添加产品", notes = "通过 Excel 文件导入，添加新的产品")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PostMapping(value = "/import", headers = "content-type=multipart/form-data")
    public Result importProduct(@ApiParam(value = "文件", required = true) MultipartFile multipartFile) throws Exception {
        return iProductService.importProduct(multipartFile);
    }

    @ApiOperation(value = "更新产品", notes = "更新产品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品Id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "productName", value = "产品名称", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "categoryId", value = "产品类别", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "comment", value = "产品备注", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PutMapping()
    public Result updateProduct(@RequestParam(value = "productId") Integer productId,
                                @RequestParam(value = "productName") String productName,
                                @RequestParam(value = "categoryId") Integer categoryId,
                                @RequestParam(value = "comment") String comment) {
        Product product = Product.builder()
                .productId(productId)
                .productName(productName)
                .categoryId(categoryId)
                .comments(comment)
                .build();
        return iProductService.updateProduct(product);
    }

    @ApiOperation(value = "上传图片", notes = "产品图片上传")
    @ApiImplicitParam(name = "productId", value = "产品ID", required = true, dataTypeClass = Integer.class)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PostMapping(value = "/pictures", headers = "content-type=multipart/form-data")
    public Result uploadPictures(@RequestParam(value = "productId") Integer productId,
                                 @RequestParam(value = "multipartFiles[]") List<MultipartFile> multipartFiles) {
        return iProductService.updatePictures(productId, multipartFiles);
    }

    @ApiOperation(value = "更新大图", notes = "产品大图更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品ID", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "multipartFile", value = "产品大图", required = true, dataTypeClass = MultipartFile.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PostMapping(value = "/head", headers = "content-type=multipart/form-data")
    public Result uploadPicture(@RequestParam(value = "productId") Integer productId,
                                @RequestParam(value = "multipartFile") MultipartFile multipartFile) {
        return iProductService.updateHeadImg(productId, multipartFile);
    }

    @ApiOperation(value = "删除图片", notes = "删除，单个、批量删除产品图片")
    @ApiImplicitParam(name = "productId", value = "产品ID", required = true, dataTypeClass = Integer.class)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @DeleteMapping("/picture")
    public Result delPictures(@RequestParam(value = "productId") Integer productId,
                              @RequestParam(value = "fileNames[]") List<String> fileNames) {
        return iProductService.deletePictures(productId, fileNames);
    }

    @ApiOperation(value = "删除产品", notes = "删除产品，单个删除，批量删除")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @DeleteMapping()
    public Result deleteProductBatch(@RequestParam("productIds[]") List<Integer> productIds) {
        return iProductService.deleteProductBatch(productIds);
    }

    @ApiOperation(value = "获取所有产品", notes = "获取所有产品, 产品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "productName", value = "产品名称，模糊查询使用", dataTypeClass = String.class),
            @ApiImplicitParam(name = "categoryId", value = "产品类别，模糊查询使用", dataTypeClass = Integer.class)
    })
    @GetMapping()
    public Result listProduct(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "productName") String productName,
                              @RequestParam(value = "categoryId") Integer categoryId) {
        return iProductService.listProduct(pageNum, pageSize, productName, categoryId);
    }

    @ApiOperation(value = "获取产品详情", notes = "获取产品详情")
    @GetMapping("/{productId}")
    public Result getDetails(@PathVariable("productId") Integer productId) {
        return iProductService.getDetails(productId);
    }
}

