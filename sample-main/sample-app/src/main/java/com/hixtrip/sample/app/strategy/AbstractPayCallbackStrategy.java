package com.hixtrip.sample.app.strategy;

import com.hixtrip.sample.app.convertor.CommandPayConvertor;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.pay.PayDomainService;

public abstract class AbstractPayCallbackStrategy implements PayCallbackStrategy{
    protected final PayDomainService payDomainService;

    protected final OrderDomainService orderDomainService;

    protected AbstractPayCallbackStrategy(PayDomainService payDomainService, OrderDomainService orderDomainService) {
        this.payDomainService = payDomainService;
        this.orderDomainService = orderDomainService;
    }

    @Override
    public final void handlePayCallback(CommandPayDTO commandPayDTO) {
        // 记录日志 可能需要注意是否会因为抛出异常导致事务回滚
        payDomainService.payRecord(CommandPayConvertor.INSTANCE.commandPayDTO2CommandPay(commandPayDTO));

        // 判断订单是否被重复支付
        if (orderDomainService.checkPayRepeat(commandPayDTO.getOrderId())) {
            throw new RuntimeException("订单不能重复支付");
        }

        this.handlePayCallbackInternal(commandPayDTO);
    }

    protected abstract void handlePayCallbackInternal(CommandPayDTO commandPayDTO);
}
