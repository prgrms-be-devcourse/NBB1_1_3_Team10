package core.application.users.service;

import static org.assertj.core.api.Assertions.*;

import core.application.movies.models.entities.*;
import core.application.movies.repositories.movie.*;
import core.application.users.models.dto.*;
import core.application.users.models.entities.*;
import core.application.users.repositories.*;
import core.application.users.repositories.mybatis.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.transaction.annotation.*;

@SpringBootTest
@Transactional
class MyPageServiceImplTest {

	@Autowired
	private MyPageServiceImpl myPageService;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private CachedMovieRepository movieRepo;
	@Autowired
	private MyBatisDibRepository dibRepo;

	private static UserEntity testUser;
	private static DibEntity testDib;
	private static CachedMovieEntity testMovie;
	private static final UUID userId = UUID.fromString("991c95d6-808a-11ef-8da5-467268b55380");
	private static final String movieId = "K-1111";

	@BeforeAll
	static void init() {

		testUser = new UserEntity(
                userId,
                "test@test.com",
                "test",
                UserRole.USER,
                "소은",
                "010-0000-0000",
                "정소은"
        );

		testDib = new DibEntity(
                null,
                userId,
                movieId
        );

		testMovie = new CachedMovieEntity(
                movieId,
                "제목1",
                "poster.jpg",
                "로맨스",
                "2024-05-12",
                "줄거리",
                "60",
                "마동석",
                "봉준호",
                1L,
                1L,
                1L,
                4L
        );
	}

	@Test
	@DisplayName("마이페이지 조회하기")
	void getMyPage() {
		// Given
		userRepo.saveNewUser(testUser);
		movieRepo.saveNewMovie(testMovie);
		dibRepo.saveNewDib(userId, testDib.getMovieId());

		// When
		MyPageRespDTO myPageRespDTO = myPageService.getMyPage(userId);

		// Then
		assertThat(myPageRespDTO.getUserEmail().equals(testUser.getUserEmail()));
		assertThat(myPageRespDTO.getAlias().equals(testUser.getAlias()));
		assertThat(myPageRespDTO.getUserName().equals(testUser.getUserName()));
		assertThat(myPageRespDTO.getRole().equals(testUser.getRole()));
		assertThat(myPageRespDTO.getPhoneNum().equals(testUser.getPhoneNum()));

		assertThat(myPageRespDTO.getDibDTOList().get(0).getMovieId().equals(testMovie.getMovieId()));
		assertThat(myPageRespDTO.getDibDTOList().get(0).getMovieTitle().equals(testMovie.getTitle()));
		assertThat(myPageRespDTO.getDibDTOList().get(0).getMoviePost().equals(testMovie.getPosterUrl()));
	}
}
