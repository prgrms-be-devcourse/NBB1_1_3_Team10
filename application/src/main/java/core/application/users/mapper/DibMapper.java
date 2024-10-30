package core.application.users.mapper;

import core.application.users.models.entities.DibEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface DibMapper {
    int saveNewDib(@Param("userId") UUID userId, @Param("movieId") String movieId);
    Optional<DibEntity> findByDibId(@Param("dibId") Long id);
    Optional<DibEntity> findByUserIdAndMovieId(@Param("userId") UUID userId, @Param("movieId") String movieId);
    Long countMovie(@Param("movieId") String movieId);
  
    List<DibEntity> selectAll();
    void deleteDibByDibId(@Param("dibId") Long dibId);
    void deleteDibByUserId(@Param("userId") UUID userId);
    void deleteBidByUserIdAndMovieId(@Param("userId") UUID userId, @Param("movieId") String movieId);

    List<DibEntity> findByUserId(@Param("userId") UUID userId);
}
