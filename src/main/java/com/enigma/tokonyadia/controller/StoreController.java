package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.model.request.NewStoreRequest;
import com.enigma.tokonyadia.model.request.SearchStoreRequest;
import com.enigma.tokonyadia.model.request.UpdateStoreRequest;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.model.response.PagingResponse;
import com.enigma.tokonyadia.model.response.StoreResponse;
import com.enigma.tokonyadia.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping(path = "/api/stores")
@Slf4j
@Tag(name = "Store", description = "Store management APIs")
public class StoreController {
    private final StoreService storeService;

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create New Store")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> create(@RequestBody NewStoreRequest request) {
        log.info("start createNewStore");
        StoreResponse storeResponse = storeService.create(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(storeResponse)
                .build();
        log.info("end createNewStore");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Get Store By Id")
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getById(@PathVariable String id) {
        log.info("start getStoreById");
        StoreResponse storeResponse = storeService.getById(id);
        CommonResponse<?> response = CommonResponse.builder()
                .data(storeResponse)
                .build();
        log.info("end getStoreById");
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get All Store")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getAll(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        log.info("start getAllStore");
        SearchStoreRequest request = SearchStoreRequest.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        Page<StoreResponse> storeResponses = storeService.getAll(request);
        PagingResponse pagingResponse = PagingResponse.builder()
                .count(storeResponses.getTotalElements())
                .totalPages(storeResponses.getTotalPages())
                .page(page)
                .size(size)
                .build();
        CommonResponse<?> response = CommonResponse.builder()
                .data(storeResponses.getContent())
                .paging(pagingResponse)
                .build();
        log.info("end getAllStore");
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update Store")
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> update(@RequestBody UpdateStoreRequest request) {
        log.info("start updateStore");
        StoreResponse storeResponse = storeService.update(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(storeResponse)
                .build();
        log.info("end updateStore");
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

}
