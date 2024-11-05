package core.application.users.service

import core.application.movies.repositories.movie.CachedMovieRepository
import core.application.users.models.dto.DibDetailRespDTO
import core.application.users.models.dto.MyPageRespDTO
import core.application.users.repositories.DibRepository
import core.application.users.repositories.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.util.*

@Service
@RequiredArgsConstructor
class MyPageServiceImpl (private val dibRepo: DibRepository,
                         private val userRepo: UserRepository,
                         private val movieRepo: CachedMovieRepository
): MyPageService {

    override fun getMyPage(userId: UUID?): MyPageRespDTO? {
        // 현재 user 불러오기 -> 나중에 수정

        val user = userRepo.findByUserId(userId)

        // user 찜 목록 불러오기
        val myDibs = dibRepo.findByUserId(userId)
        val myDibDTOs: MutableList<DibDetailRespDTO> = ArrayList()
        for (myDib in myDibs) {
            val movie = movieRepo.findByMovieId(myDib.movieId)
            val dibDetail: DibDetailRespDTO = DibDetailRespDTO(
                movieId = myDib.movieId,
                movieTitle = movie?.get()?.title,
                moviePost = movie?.get()?.posterUrl
            )

            myDibDTOs.add(dibDetail)
        }

        // DTO로 변환 -> user 정보 불러오는 로직 나중에 수정
        val myPageRespDTO = MyPageRespDTO (
            userEmail = user?.get()?.userEmail,
            alias = user?.get()?.alias,
            phoneNum = user?.get()?.phoneNum,
            userName = user?.get()?.userName,
            role = user?.get()?.role,
            dibDTOList = myDibDTOs
        )

        return myPageRespDTO
    }
}
