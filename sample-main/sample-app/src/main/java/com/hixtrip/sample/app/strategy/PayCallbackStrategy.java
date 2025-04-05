package com.hixtrip.sample.app.strategy;

import com.hixtrip.sample.client.order.dto.CommandPayDTO;

public interface PayCallbackStrategy {
    void handlePayCallback(CommandPayDTO commandPayDTO);
    boolean match(String payStatus);
}
