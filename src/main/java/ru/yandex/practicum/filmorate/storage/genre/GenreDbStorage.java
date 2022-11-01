package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage{
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        ));
    }
    @Override
    public Genre getGenreById(Integer genreId) throws GenreNotFoundException {
        if (genreId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        try {
            Genre genre = jdbcTemplate.queryForObject("SELECT * FROM genre WHERE genre_id = ?",
                    new Object[]{genreId}, (rs, rowNum) ->
                            new Genre(
                                    rs.getInt("genre_id"),
                                    rs.getString("name")
                            ));
            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException("Рейтинг с ID=" + genreId + " не найден!");
        }
    }
    @Override
    public void delete(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
    }
    @Override
    public void add(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }

    public List<Genre> getFilmGenres(Long filmId) {
        String sql = "SELECT film_genre.genre_id, genre.name FROM film_genre" +
                " INNER JOIN genre ON genre.genre_id = film_genre.genre_id WHERE film_genre.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"), rs.getString("name")), filmId
        );
    }
}
