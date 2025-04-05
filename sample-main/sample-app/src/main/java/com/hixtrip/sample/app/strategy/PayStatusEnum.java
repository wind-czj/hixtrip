package com.hixtrip.sample.app.strategy;

import lombok.Getter;

/**
 * 支付回调的状态枚举类
 */
@Getter
public enum PayStatusEnum {
    PAY_SUCCESS("PAY_SUCCESS", "支付成功"),
    PAY_FAIL("PAY_FAIL", "支付失败");

    private final String code;
    private final String msg;

    PayStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
