package com.enigma.tokonyadia.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {
    private Long count;
    private Integer totalPages;
    private Integer page;
    private Integer size;
}
