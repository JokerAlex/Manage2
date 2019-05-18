package com.dzhy.manage.entity;

import java.util.Date;

public class ProductSuk {
    private Integer skuId;

    private Integer productId;

    private String skuName;

    private Float price;

    private Date createTime;

    private Date updateTime;

    public ProductSuk(Integer skuId, Integer productId, String skuName, Float price, Date createTime, Date updateTime) {
        this.skuId = skuId;
        this.productId = productId;
        this.skuName = skuName;
        this.price = price;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public ProductSuk() {
        super();
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName == null ? null : skuName.trim();
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}