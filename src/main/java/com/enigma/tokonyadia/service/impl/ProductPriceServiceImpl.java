package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.ProductPrice;
import com.enigma.tokonyadia.repository.ProductPriceRepository;
import com.enigma.tokonyadia.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPriceServiceImpl implements ProductPriceService {
    private final ProductPriceRepository productPriceRepository;

    @Override
    public ProductPrice getById(String id) {
        return productPriceRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product price tidak ditemukan"));
    }
}
