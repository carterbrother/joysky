package com.joysky.ms.ct.designmode.strategy1.strategy;

/**
 * 价格策略接口
 */
public interface PricingStrategy {
    /**
     * 计算最终价格
     *
     * @param totalPrice 原始总价
     * @return 计算后的价格
     */
    double calculate(double totalPrice);
}
