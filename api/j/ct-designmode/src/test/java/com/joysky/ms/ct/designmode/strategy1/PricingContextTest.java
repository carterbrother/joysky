package com.joysky.ms.ct.designmode.strategy1;

import com.joysky.ms.ct.designmode.strategy1.strategy.DiscountStrategy;
import com.joysky.ms.ct.designmode.strategy1.strategy.FullReductionStrategy;
import com.joysky.ms.ct.designmode.strategy1.strategy.PricingContext;
import com.joysky.ms.ct.designmode.strategy1.strategy.PricingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PricingContextTest {

    @Test
    void testCalculateWithDiscountStrategy() {
        PricingStrategy strategy = new DiscountStrategy(0.8);
        PricingContext context = new PricingContext(strategy);
        double result = context.executeStrategy(100);
        assertEquals(80, result, 0.001);
    }

    @Test
    void testCalculateWithFullReductionStrategy() {
        PricingStrategy strategy = new FullReductionStrategy(100, 20);
        PricingContext context = new PricingContext(strategy);
        double result = context.executeStrategy(150);
        assertEquals(130, result, 0.001);
    }

    @Test
    void testSetStrategy() {
        PricingContext context = new PricingContext(new DiscountStrategy(0.9));
        double result1 = context.executeStrategy(100);
        assertEquals(90, result1, 0.001);

        context.setStrategy(new FullReductionStrategy(50, 10));
        double result2 = context.executeStrategy(100);
        assertEquals(90, result2, 0.001);
    }

    @Test
    void testNullStrategy() {
        PricingContext context = new PricingContext(null);
        assertThrows(IllegalStateException.class, () -> {
            context.executeStrategy(100);
        });
    }

    @Test
    void testNegativePriceInContext() {
        PricingStrategy strategy = new DiscountStrategy(0.8);
        PricingContext context = new PricingContext(strategy);
        assertThrows(IllegalArgumentException.class, () -> {
            context.executeStrategy(-100);
        });
    }
}
