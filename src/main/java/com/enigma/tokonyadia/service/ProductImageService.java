package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.entity.Product;
import com.enigma.tokonyadia.entity.ProductImage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    ProductImage create(Product product, MultipartFile multipartFile);
    ProductImage getById(String id);
    Resource downloadImage(String id);
    void deleteById(String id);
    void deleteAll(List<ProductImage> productImages);
}
