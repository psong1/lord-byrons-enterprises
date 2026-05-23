package com.lordbyronsenterprises.server.cart;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbyronsenterprises.server.config.JwtAuthFilter;
import com.lordbyronsenterprises.server.inventory.OutOfStockException;

@WebMvcTest(controllers = CartController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables JWT security to strictly test routing
public class CartControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void addItemToCart_ValidRequest_ReturnsOk() throws Exception {
        AddCartItemDto requestDto = new AddCartItemDto();
        requestDto.setVariantId(10L);
        requestDto.setQuantity(2);

        CartDto expectedResponse = new CartDto();
        expectedResponse.setTotal(new BigDecimal("100.00"));

        when(cartService.addItemToCart(any(), any(), any(AddCartItemDto.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/cart/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(100.0));
    }

    @Test
    void addItemToCart_OutOfStock_ReturnsBadRequest() throws Exception {
        AddCartItemDto requestDto = new AddCartItemDto();
        requestDto.setVariantId(10L);
        requestDto.setQuantity(500);

        when(cartService.addItemToCart(any(), any(), any(AddCartItemDto.class))).thenThrow(new OutOfStockException("Not enough stock."));

        mockMvc.perform(post("/cart/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest());
    }
}
