package core.application.reviews.services.images;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {

    /**
     * 이미지를 {@code cloud} 에 저장 후, 저장된 {@code URL} 을 반환하는 메서드
     *
     * @param file 이미지 데이터
     * @return {@code cloud} 에 저장된 이미지 {@code URL}
     */
    String uploadImage(MultipartFile file) throws IOException;
}
