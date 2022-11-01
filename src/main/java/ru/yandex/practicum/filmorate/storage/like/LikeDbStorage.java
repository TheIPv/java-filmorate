package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.HashSet;
import java.util.List;

@Component
public class LikeDbStorage implements LikeStorage{
    private final JdbcTemplate jdbcTemplate;
    private MpaRatingService mpaRatingService;
    private GenreService genreService;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, MpaRatingService mpaRatingService, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRatingService = mpaRatingService;
        this.genreService = genreService;
    }
    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }
    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }
    @Override
    public List<Film> getPopular(Integer count) {
        String getPopularQuery = "SELECT films.film_id, name, description, release_date, duration, mpa_id " +
                "FROM films LEFT JOIN likes ON films.film_id = likes.film_id " +
                "GROUP BY films.film_id ORDER BY COUNT(likes.user_id) DESC LIMIT ?";

        return jdbcTemplate.query(getPopularQuery, (rs, rowNum) -> {
                    try {
                        return new Film(
                                        rs.getLong("film_id"),
                                        rs.getString("name"),
                                        rs.getString("description"),
                                        rs.getDate("release_Date").toLocalDate(),
                                        rs.getInt("duration"),
                                        new HashSet<>(getLikes(rs.getLong("film_id"))),
                                        mpaRatingService.getMpaById(rs.getInt("mpa_id")),
                                        genreService.getFilmGenres(rs.getLong("film_id")));
                    } catch (MpaNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                },
                count);
    }

    @Override
    public List<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }
}
