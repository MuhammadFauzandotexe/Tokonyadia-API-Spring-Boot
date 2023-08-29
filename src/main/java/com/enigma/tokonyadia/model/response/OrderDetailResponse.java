package com.enigma.tokonyadia.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private String orderDetailId;
    private String orderId;
    private ProductResponse productResponse;
    private Integer quantity;
}
