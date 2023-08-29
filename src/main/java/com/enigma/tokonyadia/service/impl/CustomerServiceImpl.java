package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Customer;
import com.enigma.tokonyadia.entity.UserDetailsImpl;
import com.enigma.tokonyadia.model.request.SearchCustomerRequest;
import com.enigma.tokonyadia.model.request.UpdateCustomerRequest;
import com.enigma.tokonyadia.model.response.CustomerResponse;
import com.enigma.tokonyadia.repository.CustomerRepository;
import com.enigma.tokonyadia.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public Customer create(Customer customer) {
        log.info("start createCustomer");
        Customer save = customerRepository.save(customer);
        log.info("end createCustomer");
        return save;
    }

    @Override
    public Customer get(String id) {
        return findByIdOrThrowNotFound(id);
    }

    @Override
    public CustomerResponse getByAuthentication(Authentication authentication) {
        log.info("start getByAuthentication");
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        Customer customer = customerRepository.findFirstByUserCredential_Id(principal.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "user tidak ditemukan"));
        log.info("end getByAuthentication");
        return toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse getById(String id) {
        log.info("start getCustomerById");
        Customer customer = findByIdOrThrowNotFound(id);
        log.info("end getCustomerById");
        return toCustomerResponse(customer);
    }

    @Override
    public Page<CustomerResponse> getAll(SearchCustomerRequest request) {
        Specification<Customer> specification = (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(request.getKeyword())) {
                Predicate predicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getKeyword().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), request.getKeyword().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("mobilePhone")), request.getKeyword() + "%")
                );
                return query.where(predicate).getRestriction();
            }

            return query.where().getRestriction();
        };
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Customer> customerPage = customerRepository.findAll(specification, pageable);
        return customerPage.map(CustomerServiceImpl::toCustomerResponse);
    }

    @Override
    public CustomerResponse update(UpdateCustomerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerResponse customerResponse = getByAuthentication(authentication);
        if (!customerResponse.getCustomerId().equals(request.getCustomerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "anda tidak memiliki akses untuk mengakses resource ini");
        Customer customer = findByIdOrThrowNotFound(request.getCustomerId());
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setMobilePhone(request.getMobilePhone());
        customerRepository.save(customer);
        return toCustomerResponse(customer);
    }

    private Customer findByIdOrThrowNotFound(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer tidak ditemukan"));
    }

    private static CustomerResponse toCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .address(customer.getAddress())
                .mobilePhone(customer.getMobilePhone())
                .email(customer.getUserCredential().getEmail())
                .build();
    }
}
