package com.joysky.ms.ct.designmode.strategy1.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FullReductionStrategyTest {

    @Test
    void testCalculateWhenMeetCondition() {
        FullReductionStrategy strategy = new FullReductionStrategy(100, 20);
        double result = strategy.calculate(150);
        assertEquals(130, result, 0.001);
    }

    @Test
    void testCalculateWhenNotMeetCondition() {
        FullReductionStrategy strategy = new FullReductionStrategy(100, 20);
        double result = strategy.calculate(80);
        assertEquals(80, result, 0.001);
    }

    @Test
    void testCalculateWhenExactlyMeetCondition() {
        FullReductionStrategy strategy = new FullReductionStrategy(100, 20);
        double result = strategy.calculate(100);
        assertEquals(80, result, 0.001);
    }

    @Test
    void testCalculateWithNegativePrice() {
        FullReductionStrategy strategy = new FullReductionStrategy(100, 20);
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.calculate(-50);
        });
    }

    @Test
    void testConstructorWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FullReductionStrategy(-100, 20);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new FullReductionStrategy(100, -20);
        });
    }
}
