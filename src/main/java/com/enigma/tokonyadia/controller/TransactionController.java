package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.model.request.NewOrderRequest;
import com.enigma.tokonyadia.model.request.SearchOrderRequest;
import com.enigma.tokonyadia.model.request.TopUpRequest;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.model.response.OrderResponse;
import com.enigma.tokonyadia.model.response.PagingResponse;
import com.enigma.tokonyadia.model.response.TopUpResponse;
import com.enigma.tokonyadia.service.OrderService;
import com.enigma.tokonyadia.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/transactions")
@Slf4j
public class TransactionController {
    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create Transaction")
    public ResponseEntity<?> createTransaction(@RequestBody NewOrderRequest request) {
        log.info("start createTransaction - controller");
        OrderResponse orderResponse = orderService.createTransaction(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(orderResponse)
                .build();
        log.info("end createTransaction - controller");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            path = "/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get Transaction")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> getTransactionById(@PathVariable(name = "orderId") String orderId) {
        log.info("start getTransactionById - controller");
        OrderResponse orderResponse = orderService.getById(orderId);
        CommonResponse<?> response = CommonResponse.builder()
                .data(orderResponse)
                .build();
        log.info("end getTransactionById - controller");
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get All Transaction")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> getAllTransaction(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        log.info("start getTransactionById - controller");
        SearchOrderRequest request = SearchOrderRequest.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();
        Page<OrderResponse> orderResponses = orderService.getAll(request);
        PagingResponse.builder()
                .count(orderResponses.getTotalElements())
                .totalPages(orderResponses.getTotalPages())
                .page(page)
                .size(size)
                .build();
        CommonResponse<?> response = CommonResponse.builder()
                .data(orderResponses.getContent())
                .build();
        log.info("end getTransactionById - controller");
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            path = "/top-up",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> topUpWallet(@RequestBody TopUpRequest request) {
        TopUpResponse topUpResponse = paymentService.topUp(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(topUpResponse)
                .build();
        return ResponseEntity.ok(response);
    }
}
