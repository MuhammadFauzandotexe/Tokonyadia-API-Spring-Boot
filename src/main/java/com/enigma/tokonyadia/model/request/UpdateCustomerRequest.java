package com.enigma.tokonyadia.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCustomerRequest {
    @NotBlank(message = "customer id tidak boleh kosong")
    private String customerId;
    @NotBlank(message = "nama tidak boleh kosong")
    private String name;
    @NotBlank(message = "alamat tidak boleh kosong")
    private String address;
    @NotBlank(message = "nomor telepon tidak boleh kosong")
    private String mobilePhone;
}
