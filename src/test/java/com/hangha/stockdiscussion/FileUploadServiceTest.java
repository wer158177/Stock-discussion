package com.hangha.stockdiscussion;

import com.hangha.stockdiscussion.User.infrastructure.fileupload.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUploadServiceTest {

    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        fileUploadService = new FileUploadService();
    }

    @Test
    void uploadFile_Success() throws Exception {
        // 준비
        MockMultipartFile mockFile = new MockMultipartFile(
                "imageFile", "test.jpg", "image/jpeg", "test image content".getBytes()
        );

        // 동작
        String uploadedFileName = fileUploadService.uploadFile(mockFile);

        // 검증
        assertNotNull(uploadedFileName);
        assertTrue(Files.exists(Path.of(System.getProperty("user.dir") + "/uploads/" + uploadedFileName)));
    }

    @Test
    void uploadFile_Fail_EmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", "", "image/jpeg", new byte[0]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                fileUploadService.uploadFile(emptyFile)
        );

        assertEquals("파일이 비어 있습니다.", exception.getMessage());
    }
}
