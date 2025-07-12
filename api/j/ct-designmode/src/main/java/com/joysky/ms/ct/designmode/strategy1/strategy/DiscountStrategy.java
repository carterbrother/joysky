package com.joysky.ms.ct.designmode.strategy1.strategy;

/**
 * 折扣策略实现类
 */
public class DiscountStrategy implements PricingStrategy {
    private final double discountRate;

    /**
     * 构造方法
     *
     * @param discountRate 折扣率(如0.88表示88折)
     * @throws IllegalArgumentException 如果折扣率不在0-1之间
     */
    public DiscountStrategy(double discountRate) {
        if (discountRate <= 0 || discountRate > 1) {
            throw new IllegalArgumentException("折扣率必须在0到1之间");
        }
        this.discountRate = discountRate;
    }

    @Override
    public double calculate(double totalPrice) {
        if (totalPrice < 0) {
            throw new IllegalArgumentException("价格不能为负数");
        }
        return totalPrice * discountRate;
    }
}
