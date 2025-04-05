package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.infra.db.convertor.OrderDOConvertor;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import com.hixtrip.sample.infra.db.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryImpl implements OrderRepository {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void save(Order order) {
        OrderDO orderDO = OrderDOConvertor.INSTANCE.order2OrderDO(order);
        if (orderDO.getId() == null) {
            orderMapper.insert(orderDO);
        } else {
            orderMapper.updateById(orderDO);
        }
    }

    @Override
    public Order getOrderById(String orderId) {
        OrderDO orderDO = orderMapper.selectById(orderId);
        if (orderDO == null) {
            throw new RuntimeException(String.format("获取订单失败。订单id:%s", orderId));
        }
        return OrderDOConvertor.INSTANCE.orderDO2Order(orderDO);
    }
}
