package com.enigma.tokonyadia.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewOrderRequest {
    @NotBlank(message = "customer id tidak boleh kosong")
    private String customerId;
    @NotNull(message = "detail order tidak boleh kosong")
    private List<NewOrderDetailRequest> orderDetails;
}
