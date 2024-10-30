package core.application.users.service;

import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.repositories.movie.CachedMovieRepository;
import core.application.users.models.dto.DibDetailRespDTO;
import core.application.users.models.dto.MyPageRespDTO;
import core.application.users.models.entities.DibEntity;
import core.application.users.models.entities.UserEntity;
import core.application.users.repositories.DibRepository;
import core.application.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService{

    private final DibRepository dibRepo;
    private final UserRepository userRepo;
    private final CachedMovieRepository movieRepo;

    // 나중에 수정 -> 파라미터 제거
    @Override
    public MyPageRespDTO getMyPage(UUID userId) {

        // 현재 user 불러오기 -> 나중에 수정
        Optional<UserEntity> user = userRepo.findByUserId(userId);

        // user 찜 목록 불러오기
        List<DibEntity> myDibs = dibRepo.findByUserId(userId);
        List<DibDetailRespDTO> myDibDTOs = new ArrayList<>();
        for(DibEntity myDib : myDibs ){
            Optional<CachedMovieEntity> movie = movieRepo.findByMovieId(myDib.getMovieId());
            DibDetailRespDTO dibDetail = DibDetailRespDTO.builder()
                    .movieId(myDib.getMovieId())
                    .movieTitle(movie.get().getTitle())
                    .moviePost(movie.get().getPosterUrl())
                    .build();

            myDibDTOs.add(dibDetail);
        }


        // DTO로 변환 -> user 정보 불러오는 로직 나중에 수정
        MyPageRespDTO myPageRespDTO = MyPageRespDTO.builder()
                .userEmail(user.get().getUserEmail())
                .alias(user.get().getAlias())
                .phoneNum(user.get().getPhoneNum())
                .userName(user.get().getUserName())
                .role(user.get().getRole())
                .dibDTOList(myDibDTOs)
                .build();

        return myPageRespDTO;
    }
}
