package com.hangha.stockdiscussion.User.domain.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        try {
            // 업로드 디렉토리 생성 확인
            createUploadDirIfNotExists();

            // 고유 파일명 생성
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path destination = Path.of(UPLOAD_DIR + fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return fileName;  // 저장된 파일 이름 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    private void createUploadDirIfNotExists() throws IOException {
        Path uploadPath = Path.of(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);  // 디렉토리 생성
        }
    }
}