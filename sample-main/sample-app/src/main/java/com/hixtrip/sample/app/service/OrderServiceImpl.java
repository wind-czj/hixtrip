package com.hixtrip.sample.app.service;

import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.convertor.OrderConvertor;
import com.hixtrip.sample.app.strategy.PayCallbackStrategy;
import com.hixtrip.sample.app.strategy.PayCallbackStrategyFactory;
import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.domain.order.OrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * app层负责处理request请求，调用领域服务
 */
@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDomainService orderDomainService;

    @Autowired
    private PayCallbackStrategyFactory payCallbackStrategyFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(CommandOderCreateDTO commandOderCreateDTO) {
        orderDomainService.createOrder(OrderConvertor.INSTANCE.orderCreateDTO2Order(commandOderCreateDTO));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payCallback(CommandPayDTO commandPayDTO) {
        PayCallbackStrategy strategy = payCallbackStrategyFactory.getStrategy(commandPayDTO.getPayStatus());
        strategy.handlePayCallback(commandPayDTO);
    }
}
