package core.application.users.repositories.mybatis;

import core.application.users.mapper.*;
import core.application.users.models.entities.*;
import core.application.users.repositories.*;
import java.util.*;
import lombok.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Repository
@RequiredArgsConstructor
@Profile("mybatis")
public class MyBatisDibRepository implements DibRepository {

    private final DibMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public DibEntity saveNewDib(UUID userId, String movieId) {
        mapper.saveNewDib(userId, movieId);
        return mapper.findByUserIdAndMovieId(userId, movieId).orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DibEntity> findByDibId(Long id) {
        return mapper.findByDibId(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DibEntity> findByUserId(UUID userId) { return mapper.findByUserId(userId); }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DibEntity> findByUserIdAndMovieId(UUID userId, String movieId) { return mapper.findByUserIdAndMovieId(userId, movieId); }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countMovie(String movieId) {
        return mapper.countMovie(movieId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DibEntity> selectAll() {
        return mapper.selectAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDib(Long dibId) {
        mapper.deleteDibByDibId(dibId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDib(UUID userId) {
        mapper.deleteDibByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDib(UUID userId, String movieId) {
        mapper.deleteBidByUserIdAndMovieId(userId, movieId);
    }
}
