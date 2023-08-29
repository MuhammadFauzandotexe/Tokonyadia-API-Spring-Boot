package com.enigma.tokonyadia.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFileRequest {
    @NotNull(message = "image is required")
    private MultipartFile multipartFile;
    private String path;
}
