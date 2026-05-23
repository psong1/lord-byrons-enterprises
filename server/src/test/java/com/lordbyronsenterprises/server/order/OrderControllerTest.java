package com.lordbyronsenterprises.server.order;

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
import com.lordbyronsenterprises.server.payment.PaymentException;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void createOrder_Success_ReturnCreatedStatus() throws Exception {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setShippingAddressId(1L);
        requestDto.setBillingAddressId(1L);
        requestDto.setPaymentMethodId("pm_visa_card");

        OrderDto responseDto = new OrderDto();
        responseDto.setStatus(OrderStatus.PAID);

        when(orderService.createOrder(any(), any(CreateOrderRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void createOrder_PaymentDeclined_ReturnsBadRequest() throws Exception {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setBillingAddressId(1L);
        requestDto.setShippingAddressId(1L);
        requestDto.setPaymentMethodId("pm_card_declined");

        when(orderService.createOrder(any(), any(CreateOrderRequestDto.class)))
            .thenThrow(new PaymentException("Your card has insufficient funds."));

        mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest());
    }
}
