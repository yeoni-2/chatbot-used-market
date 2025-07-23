package com.example.chatbot_used_market.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MultipartFileUtil {
  public static boolean isEmptyMultipartFileList(List<MultipartFile> fileList){
    if (fileList==null) throw new RuntimeException("fileList should be not null");

    if (fileList.size()>1) return false;
    if (fileList.get(0).getOriginalFilename() == null) return true;
    return fileList.get(0).getOriginalFilename().isEmpty();
  }
}
