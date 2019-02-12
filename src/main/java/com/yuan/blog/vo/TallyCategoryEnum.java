package com.yuan.blog.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记账分类
 */
public enum TallyCategoryEnum {

    CASH("0", "现金+"),
    WEXIN_PAY("1", "微信+"),
    HUABEI("2", "花呗-"),
    ALI_PAY("3", "支付宝+"),
    XINGYE_BANK("4", "兴业银行+"),
    SHANGHAI_BANK("5", "上海银行+"),
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

    public static int explainType(String code) {
        for (TallyCategoryEnum transStatusEnum : TallyCategoryEnum.values()) {
            if (transStatusEnum.code.equals(code)) {
                String desc = transStatusEnum.getDesc();
                return desc.contains("+") ? 1 : -1;
            }
        }
        return 1;
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
