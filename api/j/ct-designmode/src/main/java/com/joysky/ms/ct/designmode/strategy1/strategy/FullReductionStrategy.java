package com.joysky.ms.ct.designmode.strategy1.strategy;

/**
 * 满减策略实现类
 */
public class FullReductionStrategy implements PricingStrategy {
    private final double fullAmount;
    private final double reductionAmount;

    /**
     * 构造方法
     *
     * @param fullAmount      满减条件金额(如100)
     * @param reductionAmount 减免金额(如30)
     * @throws IllegalArgumentException 如果参数不合法
     */
    public FullReductionStrategy(double fullAmount, double reductionAmount) {
        if (fullAmount <= 0 || reductionAmount <= 0) {
            throw new IllegalArgumentException("满减条件和减免金额必须大于0");
        }
        if (reductionAmount >= fullAmount) {
            throw new IllegalArgumentException("减免金额不能大于等于满减条件金额");
        }
        this.fullAmount = fullAmount;
        this.reductionAmount = reductionAmount;
    }

    @Override
    public double calculate(double totalPrice) {
        if (totalPrice < 0) {
            throw new IllegalArgumentException("价格不能为负数");
        }
        return totalPrice >= fullAmount ? totalPrice - reductionAmount : totalPrice;
    }
}
