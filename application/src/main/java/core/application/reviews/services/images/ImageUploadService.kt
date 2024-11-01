package core.application.reviews.services.images

import org.springframework.web.multipart.MultipartFile
import java.io.IOException

interface ImageUploadService {
    /**
     * 이미지를 `cloud` 에 저장 후, 저장된 `URL` 을 반환하는 메서드
     *
     * @param file 이미지 데이터
     * @return `cloud` 에 저장된 이미지 `URL`
     */
    @Throws(IOException::class)
    fun uploadImage(file: MultipartFile): String
}
