package com.dzhy.manage.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName ProduceVO
 * @Description 添加价值信息
 * @Author alex
 * @Date 2019-05-24
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProduceVO {
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

    /**
     * 以下为价值信息
     */

    private Float muGongWorth;

    private Float youFangWorth;

    private Float baoZhuangWorth;

    private Float teDingWorth;

    private Float beijingWorth;

    private Float beijingTedingWorth;

    private Float bendiHetongWorth;

    private Float waidiHetongWorth;


    public ProduceVO(Long produceId, Integer date, Integer productId, String produceName, Integer sukId,
                     Float sukPrice, Integer xiaDan, Integer muGong, Integer youFang, Integer baoZhuang,
                     Integer teDing, Integer beijing, Integer beijingTeding, Integer bendiHetong, Integer waidiHetong,
                     Date createTime, Date updateTime) {
        this.produceId = produceId;
        this.date = date;
        this.productId = productId;
        this.produceName = produceName;
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
        this.beijing = beijing;
        this.beijingWorth = beijing * sukPrice;
        this.beijingTeding = beijingTeding;
        this.beijingTedingWorth = beijingTeding * sukPrice;
        this.bendiHetong = bendiHetong;
        this.bendiHetongWorth = bendiHetong * sukPrice;
        this.waidiHetong = waidiHetong;
        this.waidiHetongWorth = waidiHetong * sukPrice;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
