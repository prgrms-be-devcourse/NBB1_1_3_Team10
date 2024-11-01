package core.application.reviews.controllers

import core.application.reviews.exceptions.FailedToUploadImageException
import core.application.reviews.exceptions.NoImageWereGivenExcpetion
import core.application.reviews.services.images.ImageUploadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

/**
 * 클라우드 이미지 업로드 요청과 관련된 `Controller`
 */
@RestController
@Tag(name = "CKEditor")
class ImageUploadController(private val imageUploadService: ImageUploadService) {
    /**
     * `CkEditor` 에서 이미지 업로드 요청을 `handle` 하는 엔드포인트
     *
     * @param file 이미지 파일
     * @return 클라우드에 업로드 된 이미지 `URI`
     */
    @Operation(summary = "CK Editor 이미지 업로드")
    @PostMapping("/ckeditor/image-upload") // CkEditor 의 경우 파일을 upload 라는 key 에 넣어 줌. `@RequestParam` 해서 그거 가져옴
    fun ckeditorImageUpload(@RequestParam("upload") file: MultipartFile): ResponseEntity<*> {
        // 클라우드에 저장된 이미지 URI

        val uploadedUrl = uploadImage(file)

        // CkEditor 에서 요구하는 형태로 만듬
        val response: MutableMap<String, String> = HashMap()
        response["url"] = uploadedUrl

        return ResponseEntity.ok<Map<String, String>>(response)
    }

    private fun uploadImage(file: MultipartFile): String {
        if (file == null || file.isEmpty) {
            throw NoImageWereGivenExcpetion("Image on request is null or empty.")
        } else if (file.size > MAX_FILE_SIZE) {
            throw FailedToUploadImageException(
                "Image size cannot exceed " + MAX_FILE_SIZE + " bytes."
            )
        }

        try {
            return imageUploadService.uploadImage(file)
        } catch (e: IOException) {
            throw FailedToUploadImageException(e)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ImageUploadController::class.java)

        // 50 MB
        private const val MAX_FILE_SIZE = 50 * 1000000L
    }
}
