package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.entity.UserCredential;
import com.enigma.tokonyadia.model.request.ChangePasswordRequest;
import com.enigma.tokonyadia.model.response.FileResponse;
import com.enigma.tokonyadia.model.response.UserResponse;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException;
    UserCredential getByAuthentication(Authentication authentication);

    UserResponse getByToken(Authentication authentication);
    UserResponse changePassword(ChangePasswordRequest request);
    Resource downloadProfilePicture(String imageId);
    FileResponse updateProfilePicture(MultipartFile multipartFile);
    void deleteProfilePicture(String imageId);
}
