package com.hixtrip.sample.domain.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 订单表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder(toBuilder = true)
public class Order {
    // 待支付
    public static final String PAY_STATUS_WAIT_PAY = "WAIT_PAY";
    public static final String PAY_STATUS_PAY_SUCCESS = "PAY_SUCCESS";
    public static final String PAY_STATUS_PAY_FAIL = "PAY_FAIL";

    /**
     * 订单号
     */
    private String id;


    /**
     * 购买人
     */
    private String userId;


    /**
     * SkuId
     */
    private String skuId;

    /**
     * 购买数量
     */
    private Integer amount;

    /**
     * 购买金额
     */
    private BigDecimal money;

    /**
     * 购买时间
     */
    private LocalDateTime payTime;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Long delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    // 充血模型
    // 下单
    public void create(BigDecimal skuPrice) {
        this.money = skuPrice.multiply(BigDecimal.valueOf(this.amount)).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.payTime = LocalDateTime.now();
        this.payStatus = PAY_STATUS_WAIT_PAY;
        this.delFlag = 0L;
        this.createBy = this.userId;
        this.updateBy = this.userId;
    }

    /**
     * 订单支付成功
     */
    public void paySuccess() {
        if (!PAY_STATUS_WAIT_PAY.equals(this.payStatus)) {
            throw new RuntimeException("订单状态异常");
        }

        this.payTime = LocalDateTime.now();
        this.payStatus = PAY_STATUS_PAY_SUCCESS;
    }

    /**
     * 订单支付失败
     */
    public void payFail() {
        if (!PAY_STATUS_WAIT_PAY.equals(this.payStatus)) {
            throw new RuntimeException("订单状态异常");
        }

        this.payStatus = PAY_STATUS_PAY_FAIL;
    }
}
