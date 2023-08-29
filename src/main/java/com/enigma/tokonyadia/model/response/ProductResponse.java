package com.enigma.tokonyadia.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String productId;
    private String productPriceId;
    private String productName;
    private String description;
    private Long price;
    private Integer stock;
    private StoreResponse store;
    private List<FileResponse> productImages;
}
