package core.application.reviews.services.images;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
class ImageUploadServiceTest {

    private final Logger log = Logger.getLogger(ImageUploadServiceTest.class.getName());

    @Autowired
    private ImageUploadService imageUploadService;

    @Test
    @DisplayName("랜덤 이미지를 생성해 업로드한다.")
    void uploadImage() {
        try {
            MultipartFile imageFile = genRandomImage(20, 20);

            assertThat(imageFile).isNotNull();

            String imageUrl = imageUploadService.uploadImage(imageFile);

            assertThat(imageUrl).isNotNull();
            assertThat(imageUrl.startsWith("https://storage.googleapis.com")).isTrue();

            log.info("Image were uploaded as " + imageUrl);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MultipartFile genRandomImage(int width, int height) throws IOException {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            Random random = new Random();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = random.nextInt(0xFFFFFF);
                    image.setRGB(x, y, rgb);
                }
            }

            g2d.dispose();

            ImageIO.write(image, "png", outputStream);
            outputStream.flush();

            return new MockMultipartFile("file", "random-image.png", "image/png",
                    outputStream.toByteArray());
        }
    }
}