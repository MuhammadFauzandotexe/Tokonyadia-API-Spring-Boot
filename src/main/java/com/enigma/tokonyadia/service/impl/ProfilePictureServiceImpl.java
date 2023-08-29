package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.File;
import com.enigma.tokonyadia.entity.ProfilePicture;
import com.enigma.tokonyadia.entity.UserCredential;
import com.enigma.tokonyadia.repository.ProfilePictureRepository;
import com.enigma.tokonyadia.security.UserSecurity;
import com.enigma.tokonyadia.service.FileService;
import com.enigma.tokonyadia.service.ProfilePictureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePictureServiceImpl implements ProfilePictureService {
    private final ProfilePictureRepository profilePictureRepository;
    private final FileService fileService;
    private final UserSecurity userSecurity;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProfilePicture create(UserCredential userCredential, MultipartFile multipartFile) {
        try {
            log.info("start createProfilePicture");
            ProfilePicture profilePicture = ProfilePicture.builder()
                    .user(userCredential)
                    .build();
            profilePictureRepository.saveAndFlush(profilePicture);
            File file = fileService.create(multipartFile);
            profilePicture.setName(file.getName());
            profilePicture.setSize(file.getSize());
            profilePicture.setContentType(file.getContentType());
            profilePicture.setPath(file.getPath());
            log.info("end createProfilePicture");
            return profilePicture;
        } catch (Exception exception) {
            log.error("error: createProfilePicture: {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user sudah memiliki profile picture");
        }
    }

    @Override
    public Resource download(String id) {
        log.info("start downloadProfilePicture");
        ProfilePicture profilePicture = findByIdOrThrowNotFound(id);
        Resource resource = fileService.get(profilePicture.getPath());
        log.info("start downloadProfilePicture");
        return resource;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        ProfilePicture profilePicture = findByIdOrThrowNotFound(id);
        userSecurity.validateUserById(profilePicture.getUser().getId());
        String path = profilePicture.getPath();
        profilePictureRepository.delete(profilePicture);
        fileService.delete(path);
    }

    private ProfilePicture findByIdOrThrowNotFound(String id) {
        log.info("start findByIdOrThrowNotFound-ProfilePicture");
        ProfilePicture profilePicture = profilePictureRepository
                .findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "gambar tidak ditemukan"));
        log.info("end findByIdOrThrowNotFound-ProfilePicture");
        return profilePicture;
    }
}
