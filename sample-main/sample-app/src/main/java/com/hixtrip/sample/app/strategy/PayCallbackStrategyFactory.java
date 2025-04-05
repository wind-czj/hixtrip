package com.hixtrip.sample.app.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PayCallbackStrategyFactory {
    @Autowired
    private List<PayCallbackStrategy> payCallbackStrategies;

    public PayCallbackStrategy getStrategy(String payStatus) {
        for (PayCallbackStrategy payCallbackStrategy : payCallbackStrategies) {
            if (payCallbackStrategy.match(payStatus)) {
                return payCallbackStrategy;
            }
        }
        return null;
    }
}
