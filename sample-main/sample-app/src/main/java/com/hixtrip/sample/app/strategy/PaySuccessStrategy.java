package com.hixtrip.sample.app.strategy;

import com.hixtrip.sample.app.convertor.CommandPayConvertor;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.pay.PayDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaySuccessStrategy extends AbstractPayCallbackStrategy {
    private final InventoryDomainService inventoryDomainService;

    @Autowired
    public PaySuccessStrategy(PayDomainService payDomainService, OrderDomainService orderDomainService, InventoryDomainService inventoryDomainService) {
        super(payDomainService, orderDomainService);
        this.inventoryDomainService = inventoryDomainService;
    }

    @Override
    protected void handlePayCallbackInternal(CommandPayDTO commandPayDTO) {
        // 订单状态改为支付成功
        Order order = orderDomainService.orderPaySuccess(CommandPayConvertor.INSTANCE.commandPayDTO2CommandPay(commandPayDTO));

        // 库存从预占变为占用
        inventoryDomainService.occupyFromWithholding(order.getSkuId(), order.getAmount());
    }

    @Override
    public boolean match(String payStatus) {
        return PayStatusEnum.PAY_SUCCESS.getCode().equals(payStatus);
    }
}
