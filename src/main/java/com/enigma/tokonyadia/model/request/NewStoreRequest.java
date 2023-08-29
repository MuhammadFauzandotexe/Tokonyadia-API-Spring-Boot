package com.enigma.tokonyadia.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewStoreRequest {
    @NotBlank(message = "nama toko tidak boleh kosong")
    private String storeName;
    @Schema(example = "string")
    @NotBlank(message = "nama domain tidak boleh kosong")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "domain tidak valid")
    private String domain;
    @NotBlank(message = "alamat tidak boleh kosong")
    private String address;
    @NotBlank(message = "nomor telepon tidak boleh kosong")
    private String mobilePhone;
}
