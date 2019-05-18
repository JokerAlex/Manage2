package com.dzhy.manage.entity;

import java.util.Date;

public class OutputRecord {
    private Long recordId;

    private Integer userId;

    private Integer productId;

    private Integer skuId;

    private String colName;

    private Integer value;

    private String comments;

    private Date createTime;

    private Date updateTime;

    public OutputRecord(Long recordId, Integer userId, Integer productId, Integer skuId, String colName, Integer value, String comments, Date createTime, Date updateTime) {
        this.recordId = recordId;
        this.userId = userId;
        this.productId = productId;
        this.skuId = skuId;
        this.colName = colName;
        this.value = value;
        this.comments = comments;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public OutputRecord() {
        super();
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName == null ? null : colName.trim();
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
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