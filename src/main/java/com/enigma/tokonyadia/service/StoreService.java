package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.entity.Store;
import com.enigma.tokonyadia.model.request.NewStoreRequest;
import com.enigma.tokonyadia.model.request.SearchStoreRequest;
import com.enigma.tokonyadia.model.request.UpdateStoreRequest;
import com.enigma.tokonyadia.model.response.StoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface StoreService {
    Store findById(String id);

    StoreResponse create(NewStoreRequest request);
    StoreResponse getById(String id);
    StoreResponse getBySellerId(String sellerId);
    StoreResponse getByAuthentication(Authentication authentication);
    Page<StoreResponse> getAll(SearchStoreRequest request);
    StoreResponse update(UpdateStoreRequest request);
}
