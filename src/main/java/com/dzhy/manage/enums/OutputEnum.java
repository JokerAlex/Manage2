package com.dzhy.manage.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName OutputEnum
 * @Description 产值字段枚举
 * @Author alex
 * @Date 2019-05-21
 **/
@Getter
public enum OutputEnum {

    OUTPUT_ID("outputId", "产值ID"),

    MONTH("month", "月份"),

    PRODUCT_ID("productId", "产品ID"),

    OUTPUT_NAME("outputName", "产品名称"),

    SUK_ID("sukId", "SukID"),

    SUK_PRICE("sukPrice", "SUK价格"),

    XIA_DAN("xiaDan", "下单"),

    MU_GONG("muGong", "木工"),

    YOU_FANG("youFang", "油房"),

    BAO_ZHUANG("baoZhuang", "包装"),

    TE_DING("teDing", "特定"),

    BEIJING_INPUT("beijingInput", "北京入库"),

    BEIJING_TEDING_INPUT("beijingTedingInput", "北京特定入库"),

    FACTORY_OUTPUT("factoryOutput", "工厂出货"),

    TEDING_FACTORY_OUTPUT("tedingFactoryOutput", "特定工厂出货"),

    BEIJING_STOCK("beijingStock", "北京库存"),

    BEIJING_TEDING_STOCK("beijingTedingStock", "北京特定库存"),

    ;

    private String code;
    private String name;

    OutputEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OutputEnum getOutputEnumByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        OutputEnum[] outputEnums = OutputEnum.values();
        for (OutputEnum outputEnum : outputEnums) {
            if (outputEnum.getCode().equals(code)) {
                return outputEnum;
            }
        }
        return null;
    }
}
