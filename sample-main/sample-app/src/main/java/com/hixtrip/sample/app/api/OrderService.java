package com.hixtrip.sample.app.api;

import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;

/**
 * 订单的service层
 */
public interface OrderService {
    /**
     * 下单
     * @param commandOderCreateDTO
     */
    void createOrder(CommandOderCreateDTO commandOderCreateDTO);

    /**
     * 支付结果的回调通知
     * @param commandPayDTO
     */
    void payCallback(CommandPayDTO commandPayDTO);
}
