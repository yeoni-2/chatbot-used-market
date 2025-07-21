package com.example.chatbot_used_market.service.impl;

import com.example.chatbot_used_market.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    
    @Value("${s3.bucket}")
    private String bucketName;
    
    @Value("${s3.credentials.region}")
    private String region;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 파일 확장자 검증
            validateImageFile(file);
            
            // 고유한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateUniqueFileName(fileExtension);
            
            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            // 업로드된 파일의 URL 반환
            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
            
            return imageUrl;
            
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        List<String> uploadedUrls = new ArrayList<>();
        
        if (files == null || files.isEmpty()) {
            return uploadedUrls;
        }
        
        // 모든 파일 사전 검증
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    validateImageFile(file);
                } catch (Exception e) {
                    throw new RuntimeException("파일 " + (i + 1) + " 검증 실패: " + e.getMessage(), e);
                }
            }
        }
        
        // 개별 파일 업로드 (실패 시 롤백)
        try {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                
                if (!file.isEmpty()) {
                    try {
                        String url = uploadImage(file);
                        uploadedUrls.add(url);
                    } catch (Exception e) {
                        // 실패 시 이미 업로드된 파일들 롤백
                        deleteImages(uploadedUrls);
                        
                        throw new RuntimeException("파일 " + (i + 1) + " 업로드 실패: " + e.getMessage(), e);
                    }
                }
            }
            
            return uploadedUrls;
            
        } catch (Exception e) {
            // 예외가 위에서 처리되지 않은 경우를 위한 추가 롤백
            if (!uploadedUrls.isEmpty()) {
                deleteImages(uploadedUrls);
            }
            
            throw e;
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }
        
        try {
            // URL에서 파일명 추출
            String fileName = extractFileNameFromUrl(imageUrl);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            
        } catch (Exception e) {
            throw new RuntimeException("이미지 삭제 중 오류가 발생했습니다: " + imageUrl, e);
        }
    }

    @Override
    public void deleteImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        
        for (String imageUrl : imageUrls) {
            try {
                deleteImage(imageUrl);
            } catch (Exception e) {
                // 개별 삭제 실패는 전체 작업을 중단시키지 않고 계속 진행
            }
        }
    }
    
    // AWS S3 연결 테스트 메서드
    public void testS3Connection() {
        try {
            // 버킷 존재 여부 확인
            boolean bucketExists = s3Client.headBucket(builder -> builder.bucket(bucketName))
                    .sdkHttpResponse().isSuccessful();
            
        } catch (Exception e) {
            throw new RuntimeException("S3 연결 테스트 실패: " + e.getMessage(), e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다. 현재 파일 타입: " + contentType);
        }
        
        // 파일 크기 제한 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 5MB 이하여야 합니다. 현재 크기: " + file.getSize() + " bytes");
        }
        
        // 파일명 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String generateUniqueFileName(String fileExtension) {
        return "trade-images/" + UUID.randomUUID().toString() + fileExtension;
    }

    private String extractFileNameFromUrl(String imageUrl) {
        try {
            // https://bucket-name.s3.region.amazonaws.com/trade-images/file-name 형식에서 파일명 추출
            String[] parts = imageUrl.split("/");
            if (parts.length >= 2) {
                return parts[parts.length - 2] + "/" + parts[parts.length - 1];
            }
            return parts[parts.length - 1];
        } catch (Exception e) {
            throw new RuntimeException("잘못된 이미지 URL 형식입니다: " + imageUrl, e);
        }
    }
} 