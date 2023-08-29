package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.model.request.NewOrderRequest;
import com.enigma.tokonyadia.model.request.SearchOrderRequest;
import com.enigma.tokonyadia.model.response.OrderResponse;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderResponse createTransaction(NewOrderRequest request);
    OrderResponse getById(String orderId);
    Page<OrderResponse> getAll(SearchOrderRequest request);
}
