package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.model.request.NewProductRequest;
import com.enigma.tokonyadia.model.request.SearchProductRequest;
import com.enigma.tokonyadia.model.request.UpdateProductRequest;
import com.enigma.tokonyadia.model.response.ProductResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse create(NewProductRequest request, List<MultipartFile> multipartFile);
    ProductResponse getById(String id);
    Page<ProductResponse> getAll(SearchProductRequest request);
    ProductResponse update(UpdateProductRequest request, List<MultipartFile> multipartFile);
    void deleteById(String id);
    void deleteProductImage(String imageId);
    Resource downloadProductImage(String productImage);
}
