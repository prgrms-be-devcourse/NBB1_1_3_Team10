package core.application.users.repositories.jpa;

import core.application.users.models.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface JpaDibRepository extends JpaRepository<DibEntity, Long> {

    List<DibEntity> findByUserId(UUID userId);

    Optional<DibEntity> findByUserIdAndMovieId(UUID userId, String movieId);

    Long countByMovieId(String movieId);

    void deleteAllByUserId(UUID userId);

    void deleteAllByUserIdAndMovieId(UUID userId, String movieId);
}
