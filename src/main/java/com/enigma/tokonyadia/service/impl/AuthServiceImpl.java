package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.*;
import com.enigma.tokonyadia.model.request.AuthRequest;
import com.enigma.tokonyadia.model.response.LoginResponse;
import com.enigma.tokonyadia.model.response.UserResponse;
import com.enigma.tokonyadia.repository.UserCredentialRepository;
import com.enigma.tokonyadia.security.BCryptUtils;
import com.enigma.tokonyadia.security.JwtUtils;
import com.enigma.tokonyadia.service.AdminService;
import com.enigma.tokonyadia.service.AuthService;
import com.enigma.tokonyadia.service.CustomerService;
import com.enigma.tokonyadia.service.RoleService;
import com.enigma.tokonyadia.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final BCryptUtils bCryptUtils;
    private final AdminService adminService;
    private final CustomerService customerService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse registerCustomer(AuthRequest request) {
        log.info("start registerCustomer");
        validationUtil.validate(request);

        try {
            Role role = roleService.getOrSave("1");
            UserCredential userCredential = UserCredential.builder()
                    .email(request.getEmail().toLowerCase())
                    .password(bCryptUtils.hashPassword(request.getPassword()))
                    .roles(Set.of(role))
                    .build();
            userCredentialRepository.saveAndFlush(userCredential);

            customerService.create(Customer.builder().userCredential(userCredential).build());

            log.info("end registerCustomer");
            return UserResponse.builder()
                    .userId(userCredential.getId())
                    .roles(userCredential.getRoles().stream().map(r -> r.getRole().name()).collect(Collectors.toList()))
                    .build();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email sudah terdaftar");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse registerAdmin(AuthRequest request) {
        log.info("start registerAdmin");
        validationUtil.validate(request);

        try {
            Role role = roleService.getOrSave("2");
            UserCredential userCredential = UserCredential.builder()
                    .email(request.getEmail().toLowerCase())
                    .password(bCryptUtils.hashPassword(request.getPassword()))
                    .roles(Set.of(role))
                    .build();
            userCredentialRepository.saveAndFlush(userCredential);

            adminService.create(Admin.builder()
                    .userCredential(userCredential)
                    .build());

            log.info("end registerAdmin");
            return UserResponse.builder()
                    .userId(userCredential.getId())
                    .roles(userCredential.getRoles().stream().map(r -> r.getRole().name()).collect(Collectors.toList()))
                    .build();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email sudah terdaftar");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(AuthRequest request) {
        log.info("start login");
        validationUtil.validate(request);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail().toLowerCase(),
                request.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);

        log.info("end login");
        return LoginResponse.builder()
                .token(token)
                .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();
    }
}
