package com.dzhy.manage.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Builder
@ToString
@EqualsAndHashCode
public class ProductSuk {
    private Integer sukId;

    private Integer productId;

    private String sukName;

    private Float price;

    private Date createTime;

    private Date updateTime;

    public ProductSuk(Integer sukId, Integer productId, String sukName, Float price, Date createTime, Date updateTime) {
        this.sukId = sukId;
        this.productId = productId;
        this.sukName = sukName;
        this.price = price;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public ProductSuk() {
        super();
    }

    public Integer getSukId() {
        return sukId;
    }

    public void setSukId(Integer sukId) {
        this.sukId = sukId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getSukName() {
        return sukName;
    }

    public void setSukName(String sukName) {
        this.sukName = sukName == null ? null : sukName.trim();
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