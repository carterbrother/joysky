package com.joysky.ms.ct.designmode.strategy1.param;

import java.util.List;

/**
 * 收银请求DTO
 */
public class CheckoutRequest {
    private List<com.joysky.ms.ct.designmode.strategy1.param.Item> items;
    private String strategyType;

    // getters and setters
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<com.joysky.ms.ct.designmode.strategy1.param.Item> items) {
        this.items = items;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }
}