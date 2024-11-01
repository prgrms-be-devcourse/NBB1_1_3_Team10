package core.application.reviews.controllers;

import core.application.reviews.exceptions.FailedToUploadImageException;
import core.application.reviews.exceptions.NoImageWereGivenExcpetion;
import core.application.reviews.services.images.ImageUploadService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 클라우드 이미지 업로드 요청과 관련된 {@code Controller}
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "CKEditor")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    // 50 MB
    private static final long MAX_FILE_SIZE = 50 * 1_000_000L;

    /**
     * {@code CkEditor} 에서 이미지 업로드 요청을 {@code handle} 하는 엔드포인트
     *
     * @param file 이미지 파일
     * @return 클라우드에 업로드 된 이미지 {@code URI}
     */
    @Operation(summary = "CK Editor 이미지 업로드")
    @PostMapping("/ckeditor/image-upload")
    // CkEditor 의 경우 파일을 upload 라는 key 에 넣어 줌. `@RequestParam` 해서 그거 가져옴
    public ResponseEntity<?> ckeditorImageUpload(@RequestParam("upload") MultipartFile file) {

        // 클라우드에 저장된 이미지 URI
        String uploadedUrl = uploadImage(file);

        // CkEditor 에서 요구하는 형태로 만듬
        Map<String, String> response = new HashMap<>();
        response.put("url", uploadedUrl);

        return ResponseEntity.ok(response);
    }

    private String uploadImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new NoImageWereGivenExcpetion("Image on request is null or empty.");
        } else if (file.getSize() > MAX_FILE_SIZE) {
            throw new FailedToUploadImageException(
                    "Image size cannot exceed " + MAX_FILE_SIZE + " bytes.");
        }

        try {
            return imageUploadService.uploadImage(file);
        } catch (IOException e) {
            throw new FailedToUploadImageException(e);
        }
    }
}
