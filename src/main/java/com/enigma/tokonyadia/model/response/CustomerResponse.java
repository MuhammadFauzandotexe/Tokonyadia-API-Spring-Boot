package com.enigma.tokonyadia.model.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private String customerId;
    private String name;
    private String address;
    private String mobilePhone;
    private String email;
}
