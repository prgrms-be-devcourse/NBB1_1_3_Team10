package core.application.users.repositories;

import core.application.users.models.entities.*;
import java.util.*;

/**
 * {@code DIB_TABLE} 과 관련된 {@code Repository}
 */
public interface DibRepository {

    // CREATE

    /**
     * 주어진 유저가 소유한 새로운 찜 목록을 DB 에 등록
     *
     * @param userId  찜 목록을 소유할 유저 ID
     * @param movieId 찜 목록에 들어갈 영화 ID
     * @return @return {@link DibEntity} 등록된 찜 정보
     */
    DibEntity saveNewDib(UUID userId, String movieId);

    //<editor-fold desc="READ">

    /**
     * 찜 목록 ID 로 검색
     *
     * @param id 찜 목록 ID
     * @return {@link Optional}{@code <}{@link DibEntity}{@code >}
     */
    Optional<DibEntity> findByDibId(Long id);


    /**
     * 특정 유저의 찜 목록 불러오기
     *
     * @param userId 유저 ID
     * @return 유저가 저장한 찜 목록들
     */
    List<DibEntity> findByUserId(UUID userId);

    /**
     * 특정 유저의 특정 영화 찜 목록 가져오기 {@code (???)}
     * <p>
     * 무슨 역할인지 모르겠음...
     *
     * @param userId  유저 ID
     * @param movieId 영화 ID
     * @return 찜 목록
     */
    Optional<DibEntity> findByUserIdAndMovieId(UUID userId, String movieId);

    /**
     * 특정 영화의 찜 된 횟수를 반환
     *
     * @param movieId 검색할 영화 ID
     * @return {@code Long} 해당 영화가 찜 된 횟수
     */
    Long countMovie(String movieId);

    /**
     * DB 의 모든 찜 목록을 검색
     *
     * @return {@link List}{@code <}{@link DibEntity}{@code >}
     */
    List<DibEntity> selectAll();
    //</editor-fold>

    //<editor-fold desc="DELETE">

    /**
     * 특정 찜 목록을 삭제
     *
     * @param dibId 삭제할 찜 목록의 ID
     */
    void deleteDib(Long dibId);

    /**
     * 특정 유저가 소유한 모든 찜 목록을 삭제
     *
     * @param userId 유저 ID
     */
    void deleteDib(UUID userId);

    /**
     * 특정 유저의 특정 찜 목록을 삭제
     *
     * @param userId  유저 ID
     * @param movieId 삭제할 영화 ID
     */
    void deleteDib(UUID userId, String movieId);
    //</editor-fold>
}
