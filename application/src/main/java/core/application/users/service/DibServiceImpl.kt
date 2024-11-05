package core.application.users.service

import core.application.movies.repositories.movie.CachedMovieRepository
import core.application.movies.service.MovieService
import core.application.users.models.dto.DibRespDTO
import core.application.users.repositories.DibRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@RequiredArgsConstructor
class DibServiceImpl (
    private val dibRepo: DibRepository,
    private val movieRepo: CachedMovieRepository,
    private val movieService: MovieService
):DibService {

    @Transactional
    override fun dibProcess(userId: UUID?, movieId: String?): DibRespDTO {
        val dibMovieId = movieService.getMovieDetailInfo(movieId)?.movieId
        val movie = movieRepo.findByMovieId(dibMovieId)

        // dib_table에 이미 존재하는 객체 -> 찜 취소하기
        if (dibRepo.findByUserIdAndMovieId(userId, dibMovieId).isPresent) {
            // 찜 레코드 삭제
            dibRepo.deleteDib(userId, dibMovieId)

            // dib_count 1 감소하는 로직 추가
            movie?.get()?.decrementDibCount()
            movieRepo.editMovie(dibMovieId, movie?.get())

            // DibRespDTO 생성
            return DibRespDTO(
                message = "찜 취소 완료되었습니다.",
                userId = userId,
                movieId = dibMovieId
            )
        } else {
            dibRepo.saveNewDib(userId, dibMovieId)

            // dib_count 1 증가하는 로직 추가
            movie?.get()?.incrementDibCount()
            movieRepo.editMovie(dibMovieId, movie?.get())

            // DibRespDTO 생성
            return DibRespDTO(
                message = "찜 취소 완료되었습니다.",
                userId = userId,
                movieId = dibMovieId
            )
        }
    }
}
