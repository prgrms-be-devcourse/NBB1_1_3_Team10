package core.application.users.service;

import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.repositories.movie.CachedMovieRepository;
import core.application.users.models.dto.DibRespDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Transactional
class DibServiceImplTest {

    @Autowired
    private DibService dibService;

    @Autowired
    private CachedMovieRepository movieRepo;

    @Test
    @DisplayName("찜 생성하기")
    @Transactional
    void dibCreate() {
        // Given
        // 찜 생성하기
        UUID userId = UUID.fromString("991c95d6-808a-11ef-8da5-467268b55380");
        String movieId = "K-1234";

        Optional<CachedMovieEntity> movie = movieRepo.findByMovieId(movieId);
        Long dibCnt = movie.get().getDibCount();

        // When
        DibRespDTO dibRespDTO = dibService.dibProcess(userId, movieId);

        // Then

        System.out.println(dibRespDTO.getMessage());
        System.out.println(dibRespDTO.getUserId());
        System.out.println(dibRespDTO.getMovieId());
        System.out.println(movie.get().getDibCount());

        assertThat(dibRespDTO.getMessage().equals("찜 완료되었습니다."));
        assertThat(dibRespDTO.getUserId().equals(userId));
        assertThat(dibRespDTO.getMovieId().equals(movieId));
        assertThat(movie.get().getDibCount() == dibCnt+1);
    }

    @Test
    @DisplayName("찜 취소하기")
    @Transactional
    void dibCancel() {
        // Given
        // 찜 취소하기
        UUID userId = UUID.fromString("991c95d6-808a-11ef-8da5-467268b55380");
        String movieId = "K-1234";
        dibService.dibProcess(userId, movieId); // 이미 찜 생성된 상태

        Optional<CachedMovieEntity> movie = movieRepo.findByMovieId(movieId);
        Long dibCnt = movie.get().getDibCount();

        // When
        DibRespDTO dibRespDTO = dibService.dibProcess(userId, movieId); // 다시 찜 눌렀을 때

        // Then

        System.out.println(dibRespDTO.getMessage());
        System.out.println(dibRespDTO.getUserId());
        System.out.println(dibRespDTO.getMovieId());
        System.out.println(movie.get().getDibCount());

        assertThat(dibRespDTO.getMessage().equals("찜 취소 완료되습니다."));
        assertThat(dibRespDTO.getUserId().equals(userId));
        assertThat(dibRespDTO.getMovieId().equals(movieId));
        assertThat(movie.get().getDibCount() == dibCnt-1);
    }
}
