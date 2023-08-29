package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.*;
import com.enigma.tokonyadia.model.request.NewOrderDetailRequest;
import com.enigma.tokonyadia.model.request.NewOrderRequest;
import com.enigma.tokonyadia.model.request.SearchOrderRequest;
import com.enigma.tokonyadia.model.response.*;
import com.enigma.tokonyadia.repository.OrderRepository;
import com.enigma.tokonyadia.service.CustomerService;
import com.enigma.tokonyadia.service.OrderService;
import com.enigma.tokonyadia.service.ProductPriceService;
import com.enigma.tokonyadia.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ValidationUtil validationUtil;
    private final ProductPriceService productPriceService;
    private final CustomerService customerService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse createTransaction(NewOrderRequest request) {
        log.info("start createTransactionService");
        validationUtil.validate(request);
        Customer customer = customerService.get(request.getCustomerId());

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (NewOrderDetailRequest orderDetail : request.getOrderDetails()) {
            ProductPrice productPrice = productPriceService.getById(orderDetail.getProductPriceId());

            if ((productPrice.getStock() - orderDetail.getQuantity()) < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "stock tidak boleh kurang dari 0");
            }

            OrderDetail newOrderDetail = OrderDetail.builder()
                    .productPrice(productPrice)
                    .quantity(orderDetail.getQuantity())
                    .build();
            orderDetails.add(newOrderDetail);
        }

        Order order = Order.builder()
                .customer(customer)
                .transDate(LocalDateTime.now())
                .orderDetails(orderDetails)
                .build();
        orderRepository.saveAndFlush(order);

        order.getOrderDetails().forEach(orderDetail -> {
            orderDetail.setOrder(order);
            ProductPrice productPrice = orderDetail.getProductPrice();
            productPrice.setStock(productPrice.getStock() - orderDetail.getQuantity());
        });

        log.info("end createTransactionService");
        return toOrderResponse(order);
    }

    @Override
    public OrderResponse getById(String orderId) {
        log.info("start getOrderById");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order tidak ditemukan"));
        log.info("end getOrderById");
        return toOrderResponse(order);
    }

    @Override
    public Page<OrderResponse> getAll(SearchOrderRequest request) {
        log.info("start getAllOrder");
        Specification<Order> specification = (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(request.getKeyword())) {
                Predicate predicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.join("customer").get("name")), request.getKeyword() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.join("orderDetails").join("productPrice").join("product").get("name")), request.getKeyword() + "%")
                );
                return query.where(predicate).getRestriction();
            }

            return query.getRestriction();
        };
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Order> orders = orderRepository.findAll(specification, pageable);
        log.info("end getAllOrder");
        return orders.map(this::toOrderResponse);
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {

            ProductPrice productPrice = orderDetail.getProductPrice();
            Product product = productPrice.getProduct();
            Store store = productPrice.getStore();

            StoreResponse storeResponse = StoreResponse.builder()
                    .storeId(store.getId())
                    .noSiup(store.getNoSiup())
                    .storeName(store.getName())
                    .address(store.getAddress())
                    .mobilePhone(store.getMobilePhone())
                    .domain(store.getDomain())
                    .sellerId(store.getSeller().getId())
                    .build();

            List<FileResponse> fileResponses = product.getProductImages().stream().map(productImage -> FileResponse.builder()
                    .id(productImage.getId())
                    .filename(productImage.getName())
                    .url("/api/products/image/" + productImage.getId())
                    .build()).collect(Collectors.toList());

            ProductResponse productResponse = ProductResponse.builder()
                    .productId(product.getId())
                    .productPriceId(productPrice.getId())
                    .productName(product.getName())
                    .description(product.getDescription())
                    .price(productPrice.getPrice())
                    .stock(productPrice.getStock())
                    .store(storeResponse)
                    .productImages(fileResponses)
                    .build();

            return OrderDetailResponse.builder()
                    .orderDetailId(orderDetail.getId())
                    .orderId(order.getId())
                    .productResponse(productResponse)
                    .build();
        }).collect(Collectors.toList());

        return OrderResponse.builder()
                .customerId(order.getCustomer().getId())
                .transDate(order.getTransDate())
                .orderDetails(orderDetailResponses)
                .build();
    }
}
