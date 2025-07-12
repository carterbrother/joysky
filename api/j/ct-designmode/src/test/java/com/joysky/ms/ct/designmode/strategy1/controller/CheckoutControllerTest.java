package com.joysky.ms.ct.designmode.strategy1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joysky.ms.ct.designmode.strategy1.param.CheckoutRequest;
import com.joysky.ms.ct.designmode.strategy1.param.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCheckoutWithDiscountStrategy() throws Exception {
        CheckoutRequest request = new CheckoutRequest();
        request.setItems(Arrays.asList(
                new Item("商品A", 100, 2),
                new Item("商品B", 50, 1)
        ));
        request.setStrategyType("DISCOUNT_88");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(250))
                .andExpect(jsonPath("$.finalPrice").value(220));
    }

    @Test
    void testCheckoutWithFullReductionStrategy() throws Exception {
        CheckoutRequest request = new CheckoutRequest();
        request.setItems(Arrays.asList(
                new Item("商品A", 60, 2),
                new Item("商品B", 40, 1)
        ));
        request.setStrategyType("FULL_100_REDUCE_30");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(160))
                .andExpect(jsonPath("$.finalPrice").value(130));
    }

    @Test
    void testCheckoutWithEmptyItems() throws Exception {
        CheckoutRequest request = new CheckoutRequest();
        request.setItems(Arrays.asList());
        request.setStrategyType("DISCOUNT_88");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCheckoutWithInvalidStrategy() throws Exception {
        CheckoutRequest request = new CheckoutRequest();
        request.setItems(Arrays.asList(
                new Item("商品A", 100, 1)
        ));
        request.setStrategyType("INVALID_STRATEGY");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCheckoutWithNegativePrice() throws Exception {
        CheckoutRequest request = new CheckoutRequest();
        request.setItems(Arrays.asList(
                new Item("商品A", -100, 1)
        ));
        request.setStrategyType("DISCOUNT_88");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
