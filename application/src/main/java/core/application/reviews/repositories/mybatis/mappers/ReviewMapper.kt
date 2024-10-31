package core.application.reviews.repositories.mybatis.mappers;

import core.application.reviews.models.entities.*;
import java.util.*;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ReviewMapper {

    int saveNewReview(
            @Param("movieId") String movieId,
            @Param("userId") UUID userId,
            @Param("review") ReviewEntity review
    );

    Optional<ReviewEntity> findByReviewId(Long reviewId);

    Optional<ReviewEntity> findByReviewIdWithoutContent(Long reviewId);

    List<ReviewEntity> findByMovieId(
            @Param("movieId") String movieId,
            @Param("offset") int offset,
            @Param("num") int num
    );

    List<ReviewEntity> findByMovieIdOnDateDescend(
            @Param("movieId") String movieId,
            @Param("offset") int offset,
            @Param("num") int num
    );

    List<ReviewEntity> findByMovieIdOnLikeDescend(
            @Param("movieId") String movieId,
            @Param("offset") int offset,
            @Param("num") int num
    );

    List<ReviewEntity> findByMovieIdWithoutContent(
            @Param("movieId") String movieId,
            @Param("offset") int offset,
            @Param("num") int num
    );

    List<ReviewEntity> findByMovieIdWithoutContentOnDateDescend(
            @Param("movieId") String movieId,
            @Param("offset") int offset,
            @Param("num") int num
    );

    List<ReviewEntity> findByMovieIdWithoutContentOnLikeDescend(
            @Param("movieId") String movieId,
            @Param("offset") int offset,
            @Param("num") int num
    );

    Long countByMovieId(String movieId);

    List<ReviewEntity> findByUserId(UUID userId);

    List<ReviewEntity> selectAll();

    int editReviewInfo(
            @Param("reviewId") Long reviewId,
            @Param("replacement") ReviewEntity replacement);

    int updateLikes(
            @Param("reviewId") Long reviewId,
            @Param("givenLikes") int givenLikes
    );

    void deleteReview(Long reviewId);
}
