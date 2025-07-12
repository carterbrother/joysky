package com.joysky.ms.ct.designmode.strategy1.strategy;

/**
 * 价格策略上下文类
 */
public class PricingContext {
    private PricingStrategy strategy;

    public PricingContext(PricingStrategy strategy) {
        setStrategy(strategy);
    }

    public PricingContext() {

    }

    /**
     * 设置当前策略
     *
     * @param strategy 价格策略实现
     * @throws IllegalArgumentException 如果策略为null
     */
    public void setStrategy(PricingStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("策略不能为null");
        }
        this.strategy = strategy;
    }

    /**
     * 执行策略计算
     *
     * @param totalPrice 原始总价
     * @return 计算后的价格
     * @throws IllegalStateException 如果未设置策略
     */
    public double executeStrategy(double totalPrice) {
        if (strategy == null) {
            throw new IllegalStateException("未设置价格策略");
        }
        return strategy.calculate(totalPrice);
    }
}
