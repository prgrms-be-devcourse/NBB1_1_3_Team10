package core.application.reviews.services.images;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class GCSImageUploadService implements
        ImageUploadService {

    /**
     * {@code GCS} 객체
     */
    private final Storage cloudStorage;

    /**
     * {@code GCS} 버킷 이름
     */
    @Value("${gcp.bucket.name}")
    private String cloudBucketName;

    /**
     * {@code GCS} 버킷 내 이미지가 저장될 폴더 {@code (경로)}
     */
    @Value("${gcp.bucket.upload-folder}")
    private String imageFolder;

    /**
     * {@code GCS} 이미지 저장 {@code URL} {@code prefix}
     */
    private static final String IMAGE_URL_PREFIX = "https://storage.googleapis.com";

    /**
     * {@inheritDoc}
     */
    @Override
    public String uploadImage(MultipartFile file) throws IOException {

        log.info("Uploading image to GCS");
        logFileInfo(file);

        // 클라우드에 저장될 이름 설정
        String nameToStoredInCloud = imageFolder + "/" + UUID.randomUUID();
        String contentType = file.getContentType();

        // Blob : Binary large object
        // 클라우드에 저장될 객체 정보 설정
        BlobInfo blobInfo = BlobInfo.newBuilder(cloudBucketName, nameToStoredInCloud)
                .setContentType(contentType)
                .build();

        // 클라우드에 저장 후 결과값 return
        Blob result = cloudStorage.create(blobInfo, file.getBytes());

        log.info("Image uploaded to GCS successfully");
        log.info(result.toString());

        // 우리가 설정한 대로 (버킷, 경로, 이름) 객체 저장되니까 그대로 URL return
        return IMAGE_URL_PREFIX + "/" + cloudBucketName + "/" + nameToStoredInCloud;
    }

    private void logFileInfo(MultipartFile file) {
        log.info("File info : {}", file.toString());
        log.info("Name : {}", file.getName());
        log.info("ContentType : {}", file.getContentType());

        long sizeBytes = file.getSize();
        int logarithm = ((int) Math.log10(sizeBytes)) / 3;
        Character unit = " KMGTPE".charAt(logarithm);
        double size = sizeBytes / Math.pow(1_000L, logarithm);

        log.info("Size : {}", String.format("%.2f %cB", size, unit));
    }
}
