package com.joysky.ms.ct.designmode.strategy1.param;

import java.util.List;

/**
 * 收据DTO
 */
public class Receipt {
    private List<Item> items;
    private String strategyType;
    private double totalPrice;
    private double finalPrice;

    public Receipt(List<com.joysky.ms.ct.designmode.strategy1.param.Item> items, String strategyType, double totalPrice, double finalPrice) {
        this.items = items;
        this.strategyType = strategyType;
        this.totalPrice = totalPrice;
        this.finalPrice = finalPrice;
    }

    // getters
    public List<com.joysky.ms.ct.designmode.strategy1.param.Item> getItems() {
        return items;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }
}