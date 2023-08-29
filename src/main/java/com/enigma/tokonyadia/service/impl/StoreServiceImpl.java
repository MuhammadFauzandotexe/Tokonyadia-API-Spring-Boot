package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.*;
import com.enigma.tokonyadia.model.request.NewStoreRequest;
import com.enigma.tokonyadia.model.request.SearchStoreRequest;
import com.enigma.tokonyadia.model.request.UpdateStoreRequest;
import com.enigma.tokonyadia.model.response.StoreResponse;
import com.enigma.tokonyadia.repository.StoreRepository;
import com.enigma.tokonyadia.service.RoleService;
import com.enigma.tokonyadia.service.StoreService;
import com.enigma.tokonyadia.service.UserService;
import com.enigma.tokonyadia.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final ValidationUtil validationUtil;

    @Override
    public Store findById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreResponse create(NewStoreRequest request) {
        try {
            log.info("start createStore");
            validationUtil.validate(request);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredential userCredential = userService.getByAuthentication(authentication);
            Role role = roleService.getOrSave("3");
            userCredential.addRole(role);

            Seller seller = Seller.builder()
                    .userCredential(userCredential)
                    .build();

            Store store = Store.builder()
                    .name(request.getStoreName())
                    .address(request.getAddress())
                    .domain(request.getDomain())
                    .mobilePhone(request.getMobilePhone())
                    .seller(seller)
                    .build();

            storeRepository.saveAndFlush(store);
            log.info("end createStore");
            return toStoreResponse(store);
        } catch (DataIntegrityViolationException exception) {
            log.error("duplicate data: domain | seller");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "domain sudah digunakan / anda sudah membuat toko");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public StoreResponse getById(String id) {
        log.info("start getById");
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "toko tidak ditemukan"));
        log.info("end getById");
        return toStoreResponse(store);
    }

    @Transactional(readOnly = true)
    @Override
    public StoreResponse getBySellerId(String sellerId) {
        log.info("start getStoreById");

        Store store = storeRepository.findFirstBySeller_Id(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "toko tidak ditemukan"));

        log.info("end getStoreById");
        return toStoreResponse(store);
    }

    @Transactional(readOnly = true)
    @Override
    public StoreResponse getByAuthentication(Authentication authentication) {
        log.info("start getStoreByUserId");
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        Store store = storeRepository.findFirstBySeller_UserCredential_Id(principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "toko tidak ditemukan"));
        log.info("start getStoreByUserId");
        return toStoreResponse(store);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StoreResponse> getAll(SearchStoreRequest request) {
        log.info("start getAllStore");
        Specification<Store> specification = (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(request.getKeyword())) {
                Predicate predicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getKeyword().toLowerCase() + "%"),
                        criteriaBuilder.like(root.get("mobilePhone"), request.getKeyword().toLowerCase() + "%")
                );
                return query.where(predicate).getRestriction();
            }

            return query.getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Store> stores = storeRepository.findAll(specification, pageable);
        Page<StoreResponse> storeResponses = stores.map(this::toStoreResponse);
        log.info("end getAllStore");
        return storeResponses;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoreResponse update(UpdateStoreRequest request) {
        log.info("start updateStore");
        validationUtil.validate(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        StoreResponse storeResponse = getByAuthentication(authentication);

        if (!storeResponse.getStoreId().equals(request.getStoreId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "anda tidak di izinkan untuk mengakses resource ini");

        Store store = findByIdOrThrowNotFound(request.getStoreId());
        store.setName(request.getStoreName());
        store.setAddress(request.getAddress());
        store.setNoSiup(request.getNoSiup());
        store.setDomain(request.getDomain());
        store.setMobilePhone(request.getMobilePhone());
        storeRepository.save(store);

        log.info("end updateStore");
        return toStoreResponse(store);
    }

    public Store findByIdOrThrowNotFound(String storeId) {
        log.info("start findByIdOrThrowNotFound");
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "toko tidak ditemukan"));
        log.info("end findByIdOrThrowNotFound");
        return store;
    }

    private StoreResponse toStoreResponse(Store store) {
        return StoreResponse.builder()
                .storeId(store.getId())
                .noSiup(store.getNoSiup())
                .storeName(store.getName())
                .address(store.getAddress())
                .mobilePhone(store.getMobilePhone())
                .domain(store.getDomain())
                .sellerId(store.getSeller().getId())
                .build();
    }
}
