package com.dzhy.manage.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName OutputVO
 * @Description 添加价值信息
 * @Author alex
 * @Date 2019-05-24
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputVO {
    private Long outputId;

    private Integer month;

    private Integer productId;

    private String outputName;

    private Integer sukId;

    private Float sukPrice;

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

    /**
     * 以下为价值信息
     */

    private Float muGongWorth;

    private Float youFangWorth;

    private Float baoZhuangWorth;

    private Float teDingWorth;

    private Float beijingInputWorth;

    private Float beijingTedingInputWorth;

    private Float factoryOutputWorth;

    private Float tedingFactoryOutputWorth;

    private Float beijingStockWorth;

    private Float beijingTedingStockWorth;

    public OutputVO(Long outputId, Integer month, Integer productId, String outputName,
                    Integer sukId, Float sukPrice, Integer xiaDan, Integer muGong, Integer youFang,
                    Integer baoZhuang, Integer teDing, Integer beijingInput, Integer beijingTedingInput,
                    Integer factoryOutput, Integer tedingFactoryOutput, Integer beijingStock,
                    Integer beijingTedingStock, Date createTime, Date updateTime) {
        this.outputId = outputId;
        this.month = month;
        this.productId = productId;
        this.outputName = outputName;
        this.sukId = sukId;
        this.sukPrice = sukPrice;
        this.xiaDan = xiaDan;
        this.muGong = muGong;
        this.muGongWorth = muGong * sukPrice;
        this.youFang = youFang;
        this.youFangWorth = youFang * sukPrice;
        this.baoZhuang = baoZhuang;
        this.baoZhuangWorth = baoZhuang * sukPrice;
        this.teDing = teDing;
        this.teDingWorth = teDing * sukPrice;
        this.beijingInput = beijingInput;
        this.beijingInputWorth = beijingInput * sukPrice;
        this.beijingTedingInput = beijingTedingInput;
        this.beijingTedingInputWorth = beijingTedingInput * sukPrice;
        this.factoryOutput = factoryOutput;
        this.factoryOutputWorth = factoryOutput * sukPrice;
        this.tedingFactoryOutput = tedingFactoryOutput;
        this.tedingFactoryOutputWorth = tedingFactoryOutput * sukPrice;
        this.beijingStock = beijingStock;
        this.beijingStockWorth = beijingStock * sukPrice;
        this.beijingTedingStock = beijingTedingStock;
        this.beijingTedingStockWorth = beijingTedingStock * sukPrice;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}

