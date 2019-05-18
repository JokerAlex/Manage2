package com.dzhy.manage.entity;

import java.util.Date;

public class Output {
    private Long outputId;

    private Integer productId;

    private String outputName;

    private Integer skuId;

    private Float skuPrice;

    private Integer xiaDan;

    private Integer muGong;

    private Integer youFang;

    private Integer baoZhuang;

    private Integer teDing;

    private Integer beijingInput;

    private Integer beijingTedingInput;

    private Integer factoryOutput;

    private Integer tedingFactoryOutput;

    private Integer beijingStock;

    private Integer beijingTedingStock;

    private Date createTime;

    private Date updateTime;

    public Output(Long outputId, Integer productId, String outputName, Integer skuId, Float skuPrice, Integer xiaDan, Integer muGong, Integer youFang, Integer baoZhuang, Integer teDing, Integer beijingInput, Integer beijingTedingInput, Integer factoryOutput, Integer tedingFactoryOutput, Integer beijingStock, Integer beijingTedingStock, Date createTime, Date updateTime) {
        this.outputId = outputId;
        this.productId = productId;
        this.outputName = outputName;
        this.skuId = skuId;
        this.skuPrice = skuPrice;
        this.xiaDan = xiaDan;
        this.muGong = muGong;
        this.youFang = youFang;
        this.baoZhuang = baoZhuang;
        this.teDing = teDing;
        this.beijingInput = beijingInput;
        this.beijingTedingInput = beijingTedingInput;
        this.factoryOutput = factoryOutput;
        this.tedingFactoryOutput = tedingFactoryOutput;
        this.beijingStock = beijingStock;
        this.beijingTedingStock = beijingTedingStock;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Output() {
        super();
    }

    public Long getOutputId() {
        return outputId;
    }

    public void setOutputId(Long outputId) {
        this.outputId = outputId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName == null ? null : outputName.trim();
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Float getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(Float skuPrice) {
        this.skuPrice = skuPrice;
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

    public Integer getBeijingInput() {
        return beijingInput;
    }

    public void setBeijingInput(Integer beijingInput) {
        this.beijingInput = beijingInput;
    }

    public Integer getBeijingTedingInput() {
        return beijingTedingInput;
    }

    public void setBeijingTedingInput(Integer beijingTedingInput) {
        this.beijingTedingInput = beijingTedingInput;
    }

    public Integer getFactoryOutput() {
        return factoryOutput;
    }

    public void setFactoryOutput(Integer factoryOutput) {
        this.factoryOutput = factoryOutput;
    }

    public Integer getTedingFactoryOutput() {
        return tedingFactoryOutput;
    }

    public void setTedingFactoryOutput(Integer tedingFactoryOutput) {
        this.tedingFactoryOutput = tedingFactoryOutput;
    }

    public Integer getBeijingStock() {
        return beijingStock;
    }

    public void setBeijingStock(Integer beijingStock) {
        this.beijingStock = beijingStock;
    }

    public Integer getBeijingTedingStock() {
        return beijingTedingStock;
    }

    public void setBeijingTedingStock(Integer beijingTedingStock) {
        this.beijingTedingStock = beijingTedingStock;
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