package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.model.request.ChangePasswordRequest;
import com.enigma.tokonyadia.model.response.CommonResponse;
import com.enigma.tokonyadia.model.response.FileResponse;
import com.enigma.tokonyadia.model.response.UserResponse;
import com.enigma.tokonyadia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get Self User Info")
    @GetMapping(path = "/me")
    public ResponseEntity<?> getByToken(Authentication authentication) {
        log.info("start getUserByToken");
        UserResponse userResponse = userService.getByToken(authentication);
        CommonResponse<?> response = CommonResponse.builder()
                .data(userResponse)
                .build();
        log.info("end getUserByToken");
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Change User Password")
    @PutMapping(
            path = "/change-password"
    )
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        log.info("start changePassword");
        UserResponse userResponse = userService.changePassword(request);
        CommonResponse<?> response = CommonResponse.builder()
                .data(userResponse)
                .build();
        log.info("end changePassword");
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Upload Profile Picture")
    @PutMapping(
            path = "/profile-picture"
    )
    public ResponseEntity<?> updateProfilePicture(@RequestPart(name = "image") MultipartFile multipartFile) {
        log.info("start updateProfilePicture");
        FileResponse fileResponse = userService.updateProfilePicture(multipartFile);
        CommonResponse<?> response = CommonResponse.builder()
                .data(fileResponse)
                .build();
        log.info("start updateProfilePicture");
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete Profile Picture")
    @DeleteMapping(
            path = "/profile-picture/{imageId}"
    )
    public ResponseEntity<?> deleteProfilePicture(@PathVariable(name = "imageId") String imageId) {
        log.info("start deleteProfilePicture");
        userService.deleteProfilePicture(imageId);
        CommonResponse<?> response = CommonResponse.builder()
                .data("OK")
                .build();
        log.info("end deleteProfilePicture");
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Download Profile Picture")
    @GetMapping(
            path = "/profile-picture/{imageId}"
    )
    public ResponseEntity<?> downloadProfilePicture(@PathVariable(name = "imageId") String imageId) {
        log.info("start downloadProfilePicture");
        Resource resource = userService.downloadProfilePicture(imageId);
        log.info("end downloadProfilePicture");
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
