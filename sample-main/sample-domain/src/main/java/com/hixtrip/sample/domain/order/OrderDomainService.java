package com.hixtrip.sample.domain.order;

import com.hixtrip.sample.domain.commodity.CommodityDomainService;
import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 订单领域服务
 * todo 只需要实现创建订单即可
 */
@Component
public class OrderDomainService {
    @Autowired
    private CommodityDomainService commodityDomainService;

    @Autowired
    private InventoryDomainService inventoryDomainService;

    @Autowired
    private OrderRepository orderRepository;


    /**
     * todo 需要实现
     * 创建待付款订单
     */
    public void createOrder(Order order) {
        //需要你在infra实现, 自行定义出入参
        // 预占库存
        inventoryDomainService.withholdInventory(order.getSkuId(), order.getAmount());

        // 获取sku价格
        BigDecimal skuPrice = commodityDomainService.getSkuPrice(order.getSkuId());

        // 创建订单
        order.create(skuPrice);

        // 保存订单
        orderRepository.save(order);
    }

    /**
     * todo 需要实现
     * 待付款订单支付成功
     */
    public Order orderPaySuccess(CommandPay commandPay) {
        //需要你在infra实现, 自行定义出入参
        Order order = orderRepository.getOrderById(commandPay.getOrderId());
        order.paySuccess();
        orderRepository.save(order);
        return order;
    }

    /**
     * todo 需要实现
     * 待付款订单支付失败
     */
    public Order orderPayFail(CommandPay commandPay) {
        //需要你在infra实现, 自行定义出入参
        Order order = orderRepository.getOrderById(commandPay.getOrderId());
        order.payFail();
        orderRepository.save(order);
        return order;
    }

    /**
     * 校验订单是否重复支付
     * @param orderId
     * @return
     */
    public boolean checkPayRepeat(String orderId) {
        Order order = orderRepository.getOrderById(orderId);
        if (Arrays.asList(Order.PAY_STATUS_PAY_SUCCESS, Order.PAY_STATUS_PAY_FAIL).contains(order.getPayStatus())) {
            return true;
        }

        return false;
    }
}
