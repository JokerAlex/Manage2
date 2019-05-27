package com.dzhy.manage.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Builder
@ToString
@EqualsAndHashCode
public class ProduceRecord {
    private Long recordId;

    private Integer userId;

    private Integer productId;

    private Integer sukId;

    private String colName1;

    private Integer value1;

    private String colName2;

    private Integer value2;

    private String colName3;

    private Integer value3;

    private String comments;

    private Date createTime;

    private Date updateTime;

    public ProduceRecord(Long recordId, Integer userId, Integer productId, Integer sukId, String colName1, Integer value1, String colName2, Integer value2, String colName3, Integer value3, String comments, Date createTime, Date updateTime) {
        this.recordId = recordId;
        this.userId = userId;
        this.productId = productId;
        this.sukId = sukId;
        this.colName1 = colName1;
        this.value1 = value1;
        this.colName2 = colName2;
        this.value2 = value2;
        this.colName3 = colName3;
        this.value3 = value3;
        this.comments = comments;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public ProduceRecord() {
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

    public Integer getSukId() {
        return sukId;
    }

    public void setSukId(Integer sukId) {
        this.sukId = sukId;
    }

    public String getColName1() {
        return colName1;
    }

    public void setColName1(String colName1) {
        this.colName1 = colName1 == null ? null : colName1.trim();
    }

    public Integer getValue1() {
        return value1;
    }

    public void setValue1(Integer value1) {
        this.value1 = value1;
    }

    public String getColName2() {
        return colName2;
    }

    public void setColName2(String colName2) {
        this.colName2 = colName2 == null ? null : colName2.trim();
    }

    public Integer getValue2() {
        return value2;
    }

    public void setValue2(Integer value2) {
        this.value2 = value2;
    }

    public String getColName3() {
        return colName3;
    }

    public void setColName3(String colName3) {
        this.colName3 = colName3 == null ? null : colName3.trim();
    }

    public Integer getValue3() {
        return value3;
    }

    public void setValue3(Integer value3) {
        this.value3 = value3;
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