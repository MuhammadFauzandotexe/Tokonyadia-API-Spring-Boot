package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.model.request.SearchCustomerRequest;
import com.enigma.tokonyadia.model.request.UpdateCustomerRequest;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.model.response.CustomerResponse;
import com.enigma.tokonyadia.model.response.PagingResponse;
import com.enigma.tokonyadia.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/customers")
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get CustomerById")
    public ResponseEntity<?> getById(@PathVariable String id) {
        CustomerResponse customerResponse = customerService.getById(id);
        CommonResponse<?> response = CommonResponse.builder()
                .data(customerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get All Customer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "page", required = false, defaultValue = "10") Integer size
    ) {
        log.info("start getAllCustomer");
        SearchCustomerRequest request = SearchCustomerRequest.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();
        Page<CustomerResponse> responsePage = customerService.getAll(request);
        PagingResponse pagingResponse = PagingResponse.builder()
                .count(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .page(page)
                .size(size)
                .build();
        CommonResponse<?> response = CommonResponse.builder()
                .data(responsePage.getContent())
                .paging(pagingResponse)
                .build();
        log.info("end getAllCustomer");
        return ResponseEntity.ok(response);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update Customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> update(@RequestBody UpdateCustomerRequest request) {
        log.info("start updateCustomer");
        CustomerResponse customerResponse = customerService.update(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(customerResponse)
                .build();
        log.info("end updateCustomer");
        return ResponseEntity.ok(response);
    }

}
