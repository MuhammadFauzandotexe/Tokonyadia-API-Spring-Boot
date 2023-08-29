package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.model.request.AuthRequest;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.model.response.LoginResponse;
import com.enigma.tokonyadia.model.response.UserResponse;
import com.enigma.tokonyadia.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Auth APIs")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register New Customer")
    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        log.info("start registerCustomer");
        UserResponse userResponse = authService.registerCustomer(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(userResponse)
                .build();

        log.info("end registerCustomer");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Register New Admin")
    @PostMapping(
            path = "/register-admin",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> registerAdmin(@RequestBody AuthRequest request) {
        log.info("start registerAdmin");

        UserResponse userResponse = authService.registerAdmin(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(userResponse)
                .build();
        log.info("end registerAdmin");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Login")
    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        log.info("start login");

        LoginResponse login = authService.login(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(login)
                .build();
        log.info("end login");
        return ResponseEntity.ok(response);
    }
}
