package com.dzhy.manage.controller;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.ProductSuk;
import com.dzhy.manage.service.ProductSkuService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName ProductSukController
 * @Description product suk controller
 * @Author alex
 * @Date 2019-05-29
 **/
@RestController
@RequestMapping("/suk")
@Api(value = "产品suk", description = "产品suk管理")
public class ProductSukController {

    private final ProductSkuService productSkuService;

    @Autowired
    public ProductSukController(ProductSkuService productSkuService) {
        this.productSkuService = productSkuService;
    }

    @ApiOperation(value = "添加suk", notes = "添加suk")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品Id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "sukName", value = "suk名称", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "sukPrice", value = "suk价格", required = true, dataTypeClass = Float.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'UESER')")
    @PostMapping()
    public Result insertProductSuk(@RequestParam(value = "productId") Integer productId,
                                   @RequestParam(value = "sukName")String sukName,
                                   @RequestParam(value = "sukPrice")Float sukPrice) {
        ProductSuk suk = ProductSuk.builder()
                .productId(productId)
                .sukName(sukName)
                .price(sukPrice)
                .build();
        return productSkuService.insertProductSuk(Lists.newArrayList(suk));
    }

    @ApiOperation(value = "更新suk", notes = "更新suk")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sukId", value = "sukId", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "sukName", value = "suk名称", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "sukPrice", value = "suk价格", required = true, dataTypeClass = Float.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'UESER')")
    @PutMapping
    public Result updateProductSuk(@RequestParam(value = "sukId") Integer sukId,
                                   @RequestParam(value = "sukName")String sukName,
                                   @RequestParam(value = "sukPrice")Float sukPrice) {
        ProductSuk suk = ProductSuk.builder()
                .sukId(sukId)
                .sukName(sukName)
                .price(sukPrice)
                .build();
        return productSkuService.updateProductSuk(suk);
    }

    @ApiOperation(value = "删除", notes = "删除suk")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'UESER')")
    @DeleteMapping()
    public Result deleteProductSuk(@RequestParam(value = "sukIds[]") List<Integer> sukIds) {
        return productSkuService.deleteProductSuk(sukIds);
    }

    @ApiOperation(value = "获取", notes = "获取产品所有suk")
    @ApiImplicitParam(name = "productId", value = "产品Id", required = true, dataTypeClass = Integer.class)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'UESER')")
    @GetMapping()
    public Result listProductSuk(Integer productId) {
        return productSkuService.listProductSuk(productId);
    }
}
