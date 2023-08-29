package com.enigma.tokonyadia.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductRequest {
    @NotBlank(message = "produk id itidak boleh kosong")
    private String productId;
    @NotBlank(message = "nama produk tidak boleh kosong")
    private String productName;
    @NotBlank(message = "deskripsi tidak boleh kosong")
    private String description;
    @NotNull(message = "harga tidak boleh kosong")
    @Min(value = 1, message = "harga harus lebih besar dari 0")
    private Long price;
    @Min(value = 0, message = "stok harus bilangan positif 0")
    @NotNull(message = "stok tidak boleh kosong")
    private Integer stock;
    @NotBlank(message = "store id tidak boleh kosong")
    private String storeId;
}
