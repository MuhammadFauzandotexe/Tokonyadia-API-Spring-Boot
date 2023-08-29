package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.ProfilePicture;
import com.enigma.tokonyadia.entity.UserCredential;
import com.enigma.tokonyadia.entity.UserDetailsImpl;
import com.enigma.tokonyadia.model.request.ChangePasswordRequest;
import com.enigma.tokonyadia.model.response.FileResponse;
import com.enigma.tokonyadia.model.response.UserResponse;
import com.enigma.tokonyadia.repository.UserCredentialRepository;
import com.enigma.tokonyadia.security.BCryptUtils;
import com.enigma.tokonyadia.service.ProfilePictureService;
import com.enigma.tokonyadia.service.UserService;
import com.enigma.tokonyadia.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserCredentialRepository userCredentialRepository;
    private final ProfilePictureService profilePictureService;
    private final BCryptUtils bCryptUtils;
    private final ValidationUtil validationUtil;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        log.info("start loadUserByUserId");
        UserCredential userCredential = userCredentialRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("user tidak ditemukan"));
        log.info("end loadUserByUserId");
        return UserDetailsImpl.builder()
                .userId(userCredential.getId())
                .email(userCredential.getEmail())
                .password(userCredential.getPassword())
                .authorities(userCredential.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getByToken(Authentication authentication) {
        log.info("start getByToken");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserCredential userCredential = userCredentialRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "credential tidak valid"));
        log.info("end getByToken");
        return toUserResponse(userCredential);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse changePassword(ChangePasswordRequest request) {
        log.info("start changePassword");
        validationUtil.validate(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserCredential userCredential = getByAuthentication(authentication);
        userCredential.setPassword(bCryptUtils.hashPassword(request.getNewPassword()));
        log.info("end changePassword");
        return toUserResponse(userCredential);
    }

    @Override
    public Resource downloadProfilePicture(String imageId) {
        log.info("start downloadProfilePicture");
        Resource download = profilePictureService.download(imageId);
        log.info("start downloadProfilePicture");
        return download;
    }

    @Override
    public FileResponse updateProfilePicture(MultipartFile multipartFile) {
        log.info("start updateProfilePicture");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserCredential userCredential = getByAuthentication(authentication);
        ProfilePicture profilePicture = profilePictureService.create(userCredential, multipartFile);
        log.info("end updateProfilePicture");
        return FileResponse.builder()
                .id(profilePicture.getId())
                .filename(profilePicture.getName())
                .url("/api/users/profile-picture/" + profilePicture.getId())
                .build();
    }

    @Override
    public void deleteProfilePicture(String imageId) {
        log.info("start deleteProfilePicture");
        profilePictureService.deleteById(imageId);
        log.info("end deleteProfilePicture");
    }

    @Override
    public UserCredential getByAuthentication(Authentication authentication) {
        log.info("start getByAuthentication");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserCredential userCredential = userCredentialRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "credential tidak valid"));
        log.info("end getByAuthentication");
        return userCredential;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("start loadUserByUsername");
        UserCredential userCredential = userCredentialRepository.findFirstByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("user tidak ditemukan"));
        log.info("end loadUserByUsername");
        return UserDetailsImpl.builder()
                .userId(userCredential.getId())
                .email(userCredential.getEmail())
                .password(userCredential.getPassword())
                .authorities(userCredential.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                        .collect(Collectors.toList()))
                .build();
    }


    private UserResponse toUserResponse(UserCredential userCredential) {
        return UserResponse.builder()
                .userId(userCredential.getId())
                .roles(userCredential.getRoles().stream()
                        .map(role -> role.getRole().name())
                        .collect(Collectors.toList()))
                .profilePicture(Objects.nonNull(userCredential.getProfilePicture()) ?
                        FileResponse.builder()
                                .id(userCredential.getProfilePicture().getId())
                                .filename(userCredential.getProfilePicture().getName())
                                .url("/api/users/profile-picture/" + userCredential.getProfilePicture().getId())
                                .build() : null)
                .build();
    }
}
