package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.model.request.NewProductRequest;
import com.enigma.tokonyadia.model.request.SearchProductRequest;
import com.enigma.tokonyadia.model.request.UpdateProductRequest;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.model.response.PagingResponse;
import com.enigma.tokonyadia.model.response.ProductResponse;
import com.enigma.tokonyadia.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create New Product")
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> create(
            @RequestPart(name = "product") NewProductRequest request,
            @RequestPart(name = "images") List<MultipartFile> multipartFiles
    ) {
        log.info("start createNewProduct");
        ProductResponse productResponse = productService.create(request, multipartFiles);
        CommonResponse<?> commonResponse = CommonResponse.builder()
                .data(productResponse)
                .build();
        log.info("end createNewProduct");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commonResponse);
    }

    @Operation(summary = "Get Product By Id")
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getById(@PathVariable String id) {
        log.info("start getProductById");
        ProductResponse productResponse = productService.getById(id);
        CommonResponse<?> commonResponse = CommonResponse.builder()
                .data(productResponse)
                .build();
        log.info("end getProductById");
        return ResponseEntity.status(HttpStatus.OK)
                .body(commonResponse);
    }

    @Operation(summary = "Get All Product")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getAll(
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "minPrice", required = false) Integer minPrice,
            @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        log.info("start getAllProduct");
        SearchProductRequest request = SearchProductRequest.builder()
                .productName(productName)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .page(page)
                .size(size)
                .build();
        Page<ProductResponse> productResponses = productService.getAll(request);
        PagingResponse pagingResponse = PagingResponse.builder()
                .count(productResponses.getTotalElements())
                .totalPages(productResponses.getTotalPages())
                .page(page)
                .size(size)
                .build();
        CommonResponse<?> commonResponse = CommonResponse.builder()
                .data(productResponses.getContent())
                .paging(pagingResponse)
                .build();
        log.info("end getAllProduct");
        return ResponseEntity.status(HttpStatus.OK)
                .body(commonResponse);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update Product")
    @PutMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> update(
            @RequestPart(name = "product") UpdateProductRequest request,
            @RequestPart(name = "images", required = false) List<MultipartFile> multipartFiles
    ) {
        log.info("start updateProduct");
        ProductResponse productResponse = productService.update(request, multipartFiles);
        CommonResponse<?> response = CommonResponse.builder()
                .data(productResponse)
                .build();
        log.info("end updateProduct");
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete Product By Id")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        log.info("start deleteProductById");
        productService.deleteById(id);
        CommonResponse<?> response = CommonResponse.builder()
                .data("OK")
                .build();
        log.info("end deleteProductById");
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping(
            path = "/image/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete Product Image By Image Id")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> deleteImageById(@PathVariable String id) {
        log.info("start deleteImageById");
        productService.deleteProductImage(id);
        CommonResponse<?> response = CommonResponse.builder()
                .data("OK")
                .build();
        log.info("end deleteImageById");
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping(
            path = "/image/{imageId}"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Download Image")
    public ResponseEntity<?> downloadImage(@PathVariable(name = "imageId") String imageId) {
        log.info("start downloadImage");
        Resource resource = productService.downloadProductImage(imageId);
        log.info("end downloadImage");
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
