package com.store.jewellry.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String storeImage(MultipartFile file) throws IOException;
}

