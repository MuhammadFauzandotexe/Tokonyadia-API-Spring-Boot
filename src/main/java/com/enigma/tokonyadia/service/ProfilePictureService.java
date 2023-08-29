package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.entity.ProfilePicture;
import com.enigma.tokonyadia.entity.UserCredential;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePictureService {
    ProfilePicture create(UserCredential userCredential, MultipartFile multipartFile);
    Resource download(String id);
    void deleteById(String id);
}
