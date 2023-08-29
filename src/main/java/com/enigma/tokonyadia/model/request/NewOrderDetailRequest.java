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
public class NewOrderDetailRequest {
    @NotBlank(message = "product price id tidak boleh kosong")
    private String productPriceId;

    @NotNull(message = "quantity tidak boleh kosong")
    @Min(value = 1, message = "quantity tidak boleh kurang dari 1")
    private Integer quantity;
}
