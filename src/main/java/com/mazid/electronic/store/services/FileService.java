package com.mazid.electronic.store.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FileService {
    String uploadFile(MultipartFile file, String path,String id);

    InputStream getResource(String path, String fileName) throws FileNotFoundException;


}
