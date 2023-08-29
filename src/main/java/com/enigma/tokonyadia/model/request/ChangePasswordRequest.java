package com.enigma.tokonyadia.model.request;

import com.enigma.tokonyadia.annotation.PasswordMatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PasswordMatch
public class ChangePasswordRequest {
    @NotBlank(message = "password tidak boleh kosong")
    @Size(min = 8, message = "password minimal harus berisi 8 karakter")
    private String newPassword;
    @NotBlank(message = "password tidak boleh kosong")
    private String confirmPassword;
}
