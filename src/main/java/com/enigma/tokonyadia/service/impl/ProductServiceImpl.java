package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.*;
import com.enigma.tokonyadia.model.request.NewProductRequest;
import com.enigma.tokonyadia.model.request.SearchProductRequest;
import com.enigma.tokonyadia.model.request.UpdateProductRequest;
import com.enigma.tokonyadia.model.response.FileResponse;
import com.enigma.tokonyadia.model.response.ProductResponse;
import com.enigma.tokonyadia.model.response.StoreResponse;
import com.enigma.tokonyadia.repository.ProductRepository;
import com.enigma.tokonyadia.service.ProductImageService;
import com.enigma.tokonyadia.service.ProductService;
import com.enigma.tokonyadia.service.StoreService;
import com.enigma.tokonyadia.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final ProductImageService productImageService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse create(NewProductRequest request, List<MultipartFile> multipartFiles) {
        log.info("start createProduct");
        validationUtil.validate(request);

        Store store = storeService.findById(request.getStoreId());

        ProductPrice productPrice = ProductPrice.builder()
                .price(request.getPrice())
                .stock(request.getStock())
                .store(store)
                .isActive(true)
                .build();

        Product product = Product.builder()
                .name(request.getProductName())
                .description(request.getDescription())
                .productPrices(List.of(productPrice))
                .build();

        productRepository.saveAndFlush(product);
        productPrice.setProduct(product);

        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            ProductImage productImage = productImageService.create(product, multipartFile);
            productImages.add(productImage);
        }

        product.setProductImages(productImages);

        log.info("end createProduct");
        return toStoreResponse(store, productPrice, product, product.getProductImages());
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getById(String id) {
        log.info("start getById");
        Product product = findByIdOrThrowNotFound(id);

        Optional<ProductPrice> productPrice = product.getProductPrices().stream().filter(ProductPrice::getIsActive).findFirst();
        if (productPrice.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product tidak ditemukan");

        log.info("end getById");
        return toStoreResponse(productPrice.get().getStore(), productPrice.get(), productPrice.get().getProduct(), product.getProductImages());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getAll(SearchProductRequest request) {
        log.info("start getAll");
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(request.getProductName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getProductName().toLowerCase() + "%"));
            }

            if (Objects.nonNull(request.getMinPrice())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.join("productPrices").get("price"), request.getMinPrice()));
            }

            if (Objects.nonNull(request.getMaxPrice())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.join("productPrices").get("price"), request.getMaxPrice()));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Product> products = productRepository.findAll(specification, pageable);

        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products.getContent()) {
            Optional<ProductPrice> productPrice = product.getProductPrices().stream().filter(ProductPrice::getIsActive).findFirst();
            if (productPrice.isEmpty()) continue;
            productResponses.add(toStoreResponse(productPrice.get().getStore(), productPrice.get(), productPrice.get().getProduct(), product.getProductImages()));
        }

        log.info("end getAll");
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse update(UpdateProductRequest request, List<MultipartFile> multipartFiles) {
        log.info("start updateProduct");
        validationUtil.validate(request);

        Store store = storeService.findById(request.getStoreId());

        Product product = findByIdOrThrowNotFound(request.getProductId());
        product.setName(request.getProductName());
        product.setDescription(request.getDescription());

        Optional<ProductPrice> productPrice = product.getProductPrices().stream().filter(ProductPrice::getIsActive).findFirst();

        if (productPrice.isPresent() && (!productPrice.get().getStore().getId().equals(store.getId())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "anda tidak di izinkan untuk mengakses resource ini");

        if (productPrice.isPresent() && (!productPrice.get().getPrice().equals(request.getPrice()))) {
            productPrice.get().setStock(request.getStock());
            productPrice.get().setIsActive(false);
            ProductPrice newProductPrice = ProductPrice.builder()
                    .price(request.getPrice())
                    .stock(request.getStock())
                    .store(store)
                    .isActive(true)
                    .build();
            product.addProductPrice(newProductPrice);

            List<ProductImage> productImages = new ArrayList<>();
            if (Objects.nonNull(multipartFiles) && !multipartFiles.isEmpty()) {
                for (MultipartFile multipartFile : multipartFiles) {
                    productImages.add(productImageService.create(product, multipartFile));
                }
            }

            if (!productImages.isEmpty()) product.addAllProductImage(productImages);
            newProductPrice.setProduct(product);
            productRepository.save(product);

            log.info("end updateProduct");
            return toStoreResponse(newProductPrice.getStore(), newProductPrice, product, product.getProductImages());
        }

        if (productPrice.isPresent()) {
            productPrice.get().setStock(request.getStock());
            List<ProductImage> productImages = new ArrayList<>();

            if (Objects.nonNull(multipartFiles) && !multipartFiles.isEmpty()) {
                for (MultipartFile multipartFile : multipartFiles) {
                    productImages.add(productImageService.create(product, multipartFile));
                }
            }

            if (!productImages.isEmpty()) product.addAllProductImage(productImages);
            productRepository.save(product);

            log.info("end updateProduct");
            return toStoreResponse(productPrice.get().getStore(), productPrice.get(), product, product.getProductImages());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product tidak ditemukan");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        log.info("start deleteById");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        StoreResponse store = storeService.getByAuthentication(authentication);
        Product product = findByIdOrThrowNotFound(id);

        Optional<ProductPrice> productPrice = product.getProductPrices().stream().filter(ProductPrice::getIsActive).findFirst();
        if (productPrice.isPresent() && !productPrice.get().getStore().getId().equals(store.getStoreId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "anda tidak di izinkan untuk mengakses resource ini");

        productImageService.deleteAll(product.getProductImages());
        productRepository.delete(product);
        log.info("end deleteById");
    }

    @Override
    public void deleteProductImage(String imageId) {
        log.info("start deleteProductImage");
        productImageService.deleteById(imageId);
        log.info("end deleteProductImage");
    }

    @Override
    public Resource downloadProductImage(String productImage) {
        log.info("start downloadProductImage");
        Resource resource = productImageService.downloadImage(productImage);
        log.info("end downloadProductImage");
        return resource;
    }

    private Product findByIdOrThrowNotFound(String id) {
        log.info("start findByIdOrThrowNotFound");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product tidak ditemukan"));
        log.info("end findByIdOrThrowNotFound");
        return product;
    }

    private static ProductResponse toStoreResponse(Store store, ProductPrice productPrice, Product
            product, List<ProductImage> productImages) {
        List<FileResponse> fileResponses = productImages.stream().map(productImage -> FileResponse.builder()
                .id(productImage.getId())
                .filename(productImage.getName())
                .url("/api/products/image/" + productImage.getId())
                .build()).collect(Collectors.toList());

        return ProductResponse.builder()
                .productId(product.getId())
                .productPriceId(productPrice.getId())
                .productName(product.getName())
                .description(product.getDescription())
                .price(productPrice.getPrice())
                .stock(productPrice.getStock())
                .store(StoreResponse.builder()
                        .storeId(store.getId())
                        .noSiup(store.getNoSiup())
                        .storeName(store.getName())
                        .address(store.getAddress())
                        .mobilePhone(store.getMobilePhone())
                        .domain(store.getDomain())
                        .sellerId(store.getSeller().getId())
                        .build())
                .productImages(fileResponses)
                .build();
    }
}
