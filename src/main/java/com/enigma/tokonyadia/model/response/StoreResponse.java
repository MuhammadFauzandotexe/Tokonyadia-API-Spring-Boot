package com.enigma.tokonyadia.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreResponse {
    private String storeId;
    private String noSiup;
    private String storeName;
    private String address;
    private String mobilePhone;
    private String domain;
    private String sellerId;
}
