package com.enigma.tokonyadia.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @Email(message = "email tidak valid")
    @NotBlank(message = "email tidak boleh kosong")
    private String email;
    @NotBlank(message = "password tidak boleh kosong")
    @Size(min = 8, message = "password minimal harus berisi 8 karakter")
    private String password;
}
