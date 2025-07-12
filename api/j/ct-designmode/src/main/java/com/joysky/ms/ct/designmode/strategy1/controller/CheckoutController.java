package com.joysky.ms.ct.designmode.strategy1.controller;

import com.joysky.ms.ct.designmode.strategy1.param.CheckoutRequest;
import com.joysky.ms.ct.designmode.strategy1.param.Item;
import com.joysky.ms.ct.designmode.strategy1.param.Receipt;
import com.joysky.ms.ct.designmode.strategy1.strategy.DiscountStrategy;
import com.joysky.ms.ct.designmode.strategy1.strategy.FullReductionStrategy;
import com.joysky.ms.ct.designmode.strategy1.strategy.PricingContext;
import com.joysky.ms.ct.designmode.strategy1.strategy.PricingStrategy;
import com.joysky.ms.ct.login.exception.ValidationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收银控制器
 */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final PricingContext pricingContext = new PricingContext();

    /**
     * 计算订单总价
     *
     * @param request 包含商品列表和策略类型
     * @return 收据信息
     */
    @PostMapping
    public Receipt checkout(@RequestBody CheckoutRequest request) {
        // 验证请求参数
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ValidationException("商品列表不能为空");
        }

        // 计算商品总价
        double totalPrice = calculateTotalPrice(request.getItems());

        // 设置并执行策略
        PricingStrategy strategy = createStrategy(request.getStrategyType());
        pricingContext.setStrategy(strategy);
        double finalPrice = pricingContext.executeStrategy(totalPrice);

        // 生成收据
        return new Receipt(
                request.getItems(),
                request.getStrategyType(),
                totalPrice,
                finalPrice
        );
    }

    private double calculateTotalPrice(List<Item> items) {
        if (items == null) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private PricingStrategy createStrategy(String strategyType) {
        if (strategyType == null) {
            throw new ValidationException("策略类型不能为空");
        }
        switch (strategyType) {
            case "DISCOUNT_88":
                return new DiscountStrategy(0.88);
            case "FULL_100_REDUCE_30":
                return new FullReductionStrategy(100, 30);
            default:
                throw new ValidationException("不支持的价格策略: " + strategyType);
        }
    }
}