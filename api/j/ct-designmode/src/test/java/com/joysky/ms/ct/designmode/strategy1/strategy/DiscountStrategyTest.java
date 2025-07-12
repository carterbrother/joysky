package com.joysky.ms.ct.designmode.strategy1.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscountStrategyTest {

    @Test
    void testCalculateWithValidDiscount() {
        DiscountStrategy strategy = new DiscountStrategy(0.8);
        double result = strategy.calculate(100);
        assertEquals(80, result, 0.001);
    }

    @Test
    void testCalculateWithZeroDiscount() {
        DiscountStrategy strategy = new DiscountStrategy(0);
        double result = strategy.calculate(100);
        assertEquals(0, result, 0.001);
    }

    @Test
    void testCalculateWithNegativePrice() {
        DiscountStrategy strategy = new DiscountStrategy(0.9);
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.calculate(-100);
        });
    }

    @Test
    void testConstructorWithInvalidDiscount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DiscountStrategy(1.1);
        });
    }
}
