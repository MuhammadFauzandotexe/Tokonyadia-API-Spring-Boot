package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.File;
import com.enigma.tokonyadia.entity.Product;
import com.enigma.tokonyadia.entity.ProductImage;
import com.enigma.tokonyadia.entity.ProductPrice;
import com.enigma.tokonyadia.model.response.StoreResponse;
import com.enigma.tokonyadia.repository.ProductImageRepository;
import com.enigma.tokonyadia.service.FileService;
import com.enigma.tokonyadia.service.ProductImageService;
import com.enigma.tokonyadia.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final StoreService storeService;
    private final FileService fileService;

    @Override
    public ProductImage create(Product product, MultipartFile multipartFile) {
        log.info("start createProductImage");
        File file = fileService.create(multipartFile);
        log.info("end createProductImage");
        return ProductImage.builder()
                .name(file.getName())
                .contentType(file.getContentType())
                .path(file.getPath())
                .size(file.getSize())
                .product(product)
                .build();
    }

    @Override
    public ProductImage getById(String id) {
        log.info("start getByIdProductImage");
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "gambar tidak ditemukan"));
        log.info("end getByIdProductImage");
        return productImage;
    }

    @Override
    public Resource downloadImage(String id) {
        log.info("start downloadProductImage");
        ProductImage productImage = getById(id);
        log.info("end downloadProductImage");
        return fileService.get(productImage.getPath());
    }

    @Override
    public void deleteById(String id) {
        log.info("start deleteProductImageById");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        StoreResponse store = storeService.getByAuthentication(authentication);
        ProductImage productImage = getById(id);

        Optional<ProductPrice> productPrice = productImage.getProduct().getProductPrices().stream().filter(ProductPrice::getIsActive).findFirst();
        if (productPrice.isPresent() && !productPrice.get().getStore().getId().equals(store.getStoreId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "anda tidak di izinkan untuk mengakses resource ini");


        fileService.delete(productImage.getPath());
        log.info("end deleteProductImageById");
        productImageRepository.delete(productImage);
    }

    @Override
    public void deleteAll(List<ProductImage> productImages) {
        log.info("start deleteAllProductImage");
        productImages.forEach(productImage -> {
            fileService.delete(productImage.getPath());
        });
        log.info("end deleteAllProductImage");
    }
}
