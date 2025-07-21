package com.example.chatbot_used_market.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface S3Service {

    String uploadImage(MultipartFile file);
    List<String> uploadImages(List<MultipartFile> files);
    void deleteImage(String imageUrl);
    void deleteImages(List<String> imageUrls);
} 