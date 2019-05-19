package com.dzhy.manage.entity;

import lombok.Builder;

import java.util.Date;

@Builder
public class Produce {
    private Long produceId;

    private Integer date;

    private Integer productId;

    private String produceName;

    private Integer sukId;

    private Float sukPrice;

    private Integer xiaDan;

    private Integer muGong;

    private Integer youFang;

    private Integer baoZhuang;

    private Integer teDing;

    private Integer beijing;

    private Integer beijingTeding;

    private Integer bendiHetong;

    private Integer waidiHetong;

    private Date createTime;

    private Date updateTime;

    public Produce(Long produceId, Integer date, Integer productId, String produceName, Integer sukId, Float sukPrice, Integer xiaDan, Integer muGong, Integer youFang, Integer baoZhuang, Integer teDing, Integer beijing, Integer beijingTeding, Integer bendiHetong, Integer waidiHetong, Date createTime, Date updateTime) {
        this.produceId = produceId;
        this.date = date;
        this.productId = productId;
        this.produceName = produceName;
        this.sukId = sukId;
        this.sukPrice = sukPrice;
        this.xiaDan = xiaDan;
        this.muGong = muGong;
        this.youFang = youFang;
        this.baoZhuang = baoZhuang;
        this.teDing = teDing;
        this.beijing = beijing;
        this.beijingTeding = beijingTeding;
        this.bendiHetong = bendiHetong;
        this.waidiHetong = waidiHetong;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Produce() {
        super();
    }

    public Long getProduceId() {
        return produceId;
    }

    public void setProduceId(Long produceId) {
        this.produceId = produceId;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProduceName() {
        return produceName;
    }

    public void setProduceName(String produceName) {
        this.produceName = produceName == null ? null : produceName.trim();
    }

    public Integer getSukId() {
        return sukId;
    }

    public void setSukId(Integer sukId) {
        this.sukId = sukId;
    }

    public Float getSukPrice() {
        return sukPrice;
    }

    public void setSukPrice(Float sukPrice) {
        this.sukPrice = sukPrice;
    }

    public Integer getXiaDan() {
        return xiaDan;
    }

    public void setXiaDan(Integer xiaDan) {
        this.xiaDan = xiaDan;
    }

    public Integer getMuGong() {
        return muGong;
    }

    public void setMuGong(Integer muGong) {
        this.muGong = muGong;
    }

    public Integer getYouFang() {
        return youFang;
    }

    public void setYouFang(Integer youFang) {
        this.youFang = youFang;
    }

    public Integer getBaoZhuang() {
        return baoZhuang;
    }

    public void setBaoZhuang(Integer baoZhuang) {
        this.baoZhuang = baoZhuang;
    }

    public Integer getTeDing() {
        return teDing;
    }

    public void setTeDing(Integer teDing) {
        this.teDing = teDing;
    }

    public Integer getBeijing() {
        return beijing;
    }

    public void setBeijing(Integer beijing) {
        this.beijing = beijing;
    }

    public Integer getBeijingTeding() {
        return beijingTeding;
    }

    public void setBeijingTeding(Integer beijingTeding) {
        this.beijingTeding = beijingTeding;
    }

    public Integer getBendiHetong() {
        return bendiHetong;
    }

    public void setBendiHetong(Integer bendiHetong) {
        this.bendiHetong = bendiHetong;
    }

    public Integer getWaidiHetong() {
        return waidiHetong;
    }

    public void setWaidiHetong(Integer waidiHetong) {
        this.waidiHetong = waidiHetong;
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