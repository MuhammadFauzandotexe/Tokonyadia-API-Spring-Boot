package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.entity.Customer;
import com.enigma.tokonyadia.model.request.SearchCustomerRequest;
import com.enigma.tokonyadia.model.request.UpdateCustomerRequest;
import com.enigma.tokonyadia.model.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface CustomerService {
    Customer create(Customer customer);
    Customer get(String id);

    CustomerResponse getByAuthentication(Authentication authentication);
    CustomerResponse getById(String id);
    Page<CustomerResponse> getAll(SearchCustomerRequest request);
    CustomerResponse update(UpdateCustomerRequest request);
}
