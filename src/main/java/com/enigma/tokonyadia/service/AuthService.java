package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.model.request.AuthRequest;
import com.enigma.tokonyadia.model.response.LoginResponse;
import com.enigma.tokonyadia.model.response.UserResponse;

public interface AuthService {
    UserResponse registerCustomer(AuthRequest request);
    UserResponse registerAdmin(AuthRequest request);
    LoginResponse login(AuthRequest request);

}
