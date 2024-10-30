package core.application.users.repositories.jpa.repositories;

import core.application.users.models.entities.*;
import core.application.users.repositories.*;
import core.application.users.repositories.jpa.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class DibRepositoryJpaImpl implements DibRepository {

    private final JpaDibRepository jpaRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public DibEntity saveNewDib(UUID userId, String movieId) {
        DibEntity data = DibEntity.builder()
                .userId(userId)
                .movieId(movieId)
                .build();

        return jpaRepo.save(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DibEntity> findByDibId(Long id) {
        return jpaRepo.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DibEntity> findByUserId(UUID userId) {
        return jpaRepo.findByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DibEntity> findByUserIdAndMovieId(UUID userId, String movieId) {
        return jpaRepo.findByUserIdAndMovieId(userId, movieId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countMovie(String movieId) {
        return jpaRepo.countByMovieId(movieId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DibEntity> selectAll() {
        return jpaRepo.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDib(Long dibId) {
        jpaRepo.deleteById(dibId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDib(UUID userId) {
        jpaRepo.deleteAllByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDib(UUID userId, String movieId) {
        jpaRepo.deleteAllByUserIdAndMovieId(userId, movieId);
    }
}
