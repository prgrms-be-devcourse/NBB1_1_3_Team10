package core.application.movies.contoller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.context.support.WithMockUser;
import core.application.movies.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import core.application.movies.controller.MovieController;
import core.application.movies.models.dto.response.MovieDetailRespDTO;

@WebMvcTest(MovieController.class)
public class MovieContollerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Test
    @DisplayName("영화 상세 정보 가져오기 컨트롤러 테스트")
    @WithMockUser(username = "user", roles = {"USER"}) // Mock 사용자 생성: username이 "user"이고 역할이 "USER"인 사용자로 인증
    public void viewMovieDetails_ReturnsMovieDetails() throws Exception {
        // given
        String movieId = "K-15172"; // 테스트에 사용할 영화 ID
        MovieDetailRespDTO movieDetail = new MovieDetailRespDTO(
                movieId,
                "검사외전",
                "http://file.koreafilm.or.kr/thm/02/00/04/23/tn_DPK011412.jpg",
                "드라마,액션,코메디,범죄",
                "20160203",
                "진실 앞에 무대뽀! 다혈질검사, 살인 누명을 쓰고 감옥에 갇히다!거친 수사 방식으로 유명한 다혈질 검사 ‘변재욱’(황정민). 취조 중이던 피의자가 변사체로 발견 되면서 살인 혐의로 체포된다. 꼼짝없이 살인 누명을 쓰게 된 변재욱은 정당방위에 의한 무죄를 약속하는 차장검사 ‘종길’(이성민)을 믿고 진실 앞에 눈감고 모든 혐의를 인정하지만 그 순간 모든 증거는 사라지고 그는 결국 15년 형을 받고 수감 된다.진실 따위 나 몰라라! 허세남발 꽃미남 사기꾼, 반격 작전에 선수로 기용되다!감옥에서 복수의 칼을 갈던 재욱. 5년 후, 자신이 누명을 쓰게 된 사건에 대해 알고 있는 허세남발 꽃미남 사기꾼 ‘치원’(강동원)을 우연히 만나게 되고, 그 순간 감옥 밖 작전을 대행해 줄 선수임을 직감한다. 검사 노하우를 총 동원, 치원을 무혐의로 내보내고 반격을 준비하는 재욱. 하지만 자유를 얻은 치원은 재욱에게서 벗어날 기회만 호시탐탐 노리는데…감옥에 갇힌 검사와 세상 밖으로 나온 사기꾼!이들의 예측불허, 반격의 한탕은 성공할 수 있을까?",
                "126",
                "황정민, 강동원, 이성민, 박성웅, 김응수",
                "이일형",
                0L,
                0L,
                0L,
                0L
        );

        // when: movieService에서 getMovieDetailInfo 메서드를 호출할 때 위에서 정의한 movieDetail을 반환하도록 설정
        given(movieService.getMovieDetailInfo(movieId)).willReturn(movieDetail);

        // when & then
        // MockMvc를 사용하여 GET 요청을 수행하고 응답을 검증
        mockMvc.perform(get("/movies/{movieId}", movieId) // 요청할 URL
                        .contentType(MediaType.APPLICATION_JSON)) // 요청의 Content-Type을 JSON으로 설정
                .andExpect(status().isOk()) // 응답 상태 코드가 200 OK인지 검증
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 응답 Content-Type이 JSON인지 검증
                // 응답 JSON에서 예상 값과 일치하는지 검증
                .andExpect(jsonPath("$.result.movieId").value(movieId))
                .andExpect(jsonPath("$.result.title").value("검사외전"))
                .andExpect(jsonPath("$.result.posterUrl").value("http://file.koreafilm.or.kr/thm/02/00/04/23/tn_DPK011412.jpg"))
                .andExpect(jsonPath("$.result.genre").value("드라마,액션,코메디,범죄"))
                .andExpect(jsonPath("$.result.releaseDate").value("20160203"))
                .andExpect(jsonPath("$.result.plot").value("진실 앞에 무대뽀! 다혈질검사, 살인 누명을 쓰고 감옥에 갇히다!거친 수사 방식으로 유명한 다혈질 검사 ‘변재욱’(황정민). 취조 중이던 피의자가 변사체로 발견 되면서 살인 혐의로 체포된다. 꼼짝없이 살인 누명을 쓰게 된 변재욱은 정당방위에 의한 무죄를 약속하는 차장검사 ‘종길’(이성민)을 믿고 진실 앞에 눈감고 모든 혐의를 인정하지만 그 순간 모든 증거는 사라지고 그는 결국 15년 형을 받고 수감 된다.진실 따위 나 몰라라! 허세남발 꽃미남 사기꾼, 반격 작전에 선수로 기용되다!감옥에서 복수의 칼을 갈던 재욱. 5년 후, 자신이 누명을 쓰게 된 사건에 대해 알고 있는 허세남발 꽃미남 사기꾼 ‘치원’(강동원)을 우연히 만나게 되고, 그 순간 감옥 밖 작전을 대행해 줄 선수임을 직감한다. 검사 노하우를 총 동원, 치원을 무혐의로 내보내고 반격을 준비하는 재욱. 하지만 자유를 얻은 치원은 재욱에게서 벗어날 기회만 호시탐탐 노리는데…감옥에 갇힌 검사와 세상 밖으로 나온 사기꾼!이들의 예측불허, 반격의 한탕은 성공할 수 있을까?"))
                .andExpect(jsonPath("$.result.runningTime").value("126"))
                .andExpect(jsonPath("$.result.actors").value("황정민, 강동원, 이성민, 박성웅, 김응수"))
                .andExpect(jsonPath("$.result.director").value("이일형"))
                .andExpect(jsonPath("$.result.dibCount").value(0L))
                .andExpect(jsonPath("$.result.reviewCount").value(0L))
                .andExpect(jsonPath("$.result.commentCount").value(0L))
                .andExpect(jsonPath("$.result.sumOfRating").value(0L));
    }
}