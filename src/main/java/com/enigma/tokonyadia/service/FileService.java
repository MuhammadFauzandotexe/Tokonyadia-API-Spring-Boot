package com.enigma.tokonyadia.service;

import com.enigma.tokonyadia.entity.File;
import com.enigma.tokonyadia.model.request.UpdateFileRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    File create(MultipartFile multipartFile);
    List<File> createBulk(List<MultipartFile> multipartFiles);
    Resource get(String path);
    void delete(String path);

}
