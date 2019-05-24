package com.dzhy.manage.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName ProduceEnum
 * @Description 进度枚举字段
 * @Author alex
 * @Date 2019-05-24
 **/
@Getter
public enum ProduceEnum {

    PRODUCE_ID("produceId", "产值ID"),

    DATE("date", "月份"),

    PRODUCT_ID("productId", "产品ID"),

    PRODUCE_NAME("produceName", "产品名称"),

    SUK_ID("sukId", "SukID"),

    SUK_PRICE("sukPrice", "Suk价格"),

    PRODUCE_PRICE("price", "价值"),

    XIA_DAN("xiaDan", "下单"),

    MU_GONG("muGong", "木工"),

    YOU_FANG("youFang", "油房"),

    BAO_ZHUANG("baoZhuang", "包装"),

    TE_DING("teDing", "特定"),

    BEIJING("beijing", "北京"),

    BEIJING_TEDING("beijingTeding", "北京特定"),

    BENDI_HETONG("bendiHetong", "本地合同"),

    WAIDI_HETONG("waidiHetong", "外地合同"),

            ;

    private String code;
    private String name;

    ProduceEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ProduceEnum getProduceEnumByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        ProduceEnum[] produceEnums = ProduceEnum.values();
        for (ProduceEnum produceEnum : produceEnums) {
            if (produceEnum.getCode().equals(code)) {
                return produceEnum;
            }
        }
        return null;
    }
}
