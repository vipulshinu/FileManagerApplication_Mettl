package com.fileStorage.fileManager.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IFileManagerService {

    void saveUploadedFile(MultipartFile file);
    byte[] viewUploadedFile(String file);
    void deleteUploadedFile(String file);
    List<Map<String,String>> viewAllUploadedFiles();
    void updateUploadedFile(String file, Map<String,String> textData);
}
