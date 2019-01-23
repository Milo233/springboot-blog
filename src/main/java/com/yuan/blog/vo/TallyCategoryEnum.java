package com.yuan.blog.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记账分类
 */
public enum TallyCategoryEnum {

    SHANGHAI_BANK("0", "上海银行+"),
    ALI_PAY("1", "支付宝+"),
    WEXIN_PAY("2", "微信+"),
    JIUJIANG_BANK("3", "九江银行+"),
    XINGYE_BANK("4", "兴业银行+"),
    CASH("4", "现金+"),
    HUABEI("5", "花呗-"),
    JIAOTONG_CREDIT_CARD("6", "交行信用卡-"),
    ZHAOSHANG_CREDIT_CARD("7", "招商信用卡-");

    private final String code;
    private final String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    TallyCategoryEnum(String code, String value) {
        this.code = code;
        this.desc = value;
    }

    //传入code  返回对应的desc
    public static String explain(String code) {
        for (TallyCategoryEnum transStatusEnum : TallyCategoryEnum.values()) {
            if (transStatusEnum.code.equals(code)) {
                return transStatusEnum.getDesc();
            }
        }
        return null;
    }

    public static List<Map> toList() {
        List<Map> list = new ArrayList<>();
        for (TallyCategoryEnum transStatusEnum : TallyCategoryEnum.values()) {
            Map map = new HashMap<>();
            map.put("code", transStatusEnum.getCode());
            map.put("desc", transStatusEnum.getDesc());
            list.add(map);
        }
        return list;
    }
}
