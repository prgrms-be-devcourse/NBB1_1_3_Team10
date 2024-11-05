package core.application.reviews.services.images

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

@Service
class GCSImageUploadService(
    /**
     * `GCS` 객체
     */
    private val cloudStorage: Storage,

    @Value("\${gcp.bucket.name}")
    private val cloudBucketName: String,
    @Value("\${gcp.bucket.upload-folder}")
    private val imageFolder: String
) : ImageUploadService {
    /**
     * `GCS` 버킷 이름
     */


    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun uploadImage(file: MultipartFile): String {
        log.info("Uploading image to GCS")
        logFileInfo(file)

        // 클라우드에 저장될 이름 설정
        val nameToStoredInCloud = imageFolder + "/" + UUID.randomUUID()
        val contentType = file.contentType

        // Blob : Binary large object
        // 클라우드에 저장될 객체 정보 설정
        val blobInfo = BlobInfo.newBuilder(cloudBucketName, nameToStoredInCloud)
            .setContentType(contentType)
            .build()

        // 클라우드에 저장 후 결과값 return
        val result = cloudStorage.create(blobInfo, file.bytes)

        log.info("Image uploaded to GCS successfully")
        log.info(result.toString())

        // 우리가 설정한 대로 (버킷, 경로, 이름) 객체 저장되니까 그대로 URL return
        return IMAGE_URL_PREFIX + "/" + cloudBucketName + "/" + nameToStoredInCloud
    }

    private fun logFileInfo(file: MultipartFile) {
        log.info("File info : {}", file.toString())
        log.info("Name : {}", file.name)
        log.info("ContentType : {}", file.contentType)

        val sizeBytes = file.size
        val logarithm = (log10(sizeBytes.toDouble()).toInt()) / 3
        val unit = " KMGTPE"[logarithm]
        val size: Double = sizeBytes / 1000.toDouble().pow(logarithm.toDouble())

        log.info("Size : {}", String.format("%.2f %cB", size, unit))
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(GCSImageUploadService::class.java)

        /**
         * `GCS` 이미지 저장 `URL` `prefix`
         */
        private const val IMAGE_URL_PREFIX = "https://storage.googleapis.com"
    }
}
