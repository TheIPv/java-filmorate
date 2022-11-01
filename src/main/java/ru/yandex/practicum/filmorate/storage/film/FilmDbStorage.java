package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage{
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final JdbcTemplate jdbcTemplate;
    private long uniqueId = 1;
    private MpaRatingService mpaService;
    private GenreService genreService;
    private LikeStorage likeStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaRatingService mpaService, GenreService genreService, LikeStorage likeStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.likeStorage = likeStorage;
    }

    @Override
    public Collection<Film> homeFilms() {

        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
                    try {
                        return new Film(
                                rs.getLong("film_id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getDate("release_date").toLocalDate(),
                                rs.getInt("duration"),
                                new HashSet<>(likeStorage.getLikes(rs.getLong("film_id"))),
                                mpaService.getMpaById(rs.getInt("mpa_id")),
                                genreService.getFilmGenres(rs.getLong("film_id")));
                    } catch (MpaNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Override
    public Film addFilm(Film film) throws MpaNotFoundException, GenreNotFoundException {
        if(film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Дата выхода фильма не может быть раньше, чем "
                    + EARLIEST_DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            );
        }
        if(!Objects.isNull(film.getDescription())) {
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Описание фильма не может превышать 200 символов");
            }
        }
        if(film.getName().equals("")) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if(film.getDuration() < 0) {
            throw new ValidationException("Продолжительность меньше нуля");
        }
        if(film.getMpa() == null) {
            throw new ValidationException("У фильма должен быть рейтинг");
        }
        film.setId(uniqueId); ++uniqueId;
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        jdbcTemplate.update("INSERT INTO FILMS(FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES(?,?,?,?,?,?)",
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genre.setName(genreService.getGenreById(genre.getId()).getName());
            }
            genreService.putGenres(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws MpaNotFoundException, GenreNotFoundException {
        if (film == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        if(film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Дата выхода фильма не может быть раньше, чем "
                    + EARLIEST_DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            );
        }
        if(!Objects.isNull(film.getDescription())) {
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Описание фильма не может превышать 200 символов");
            }
        }
        if(film.getMpa() == null) {
            throw new ValidationException("У фильма должен быть рейтинг");
        }
        if(film.getName().equals("")) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if(film.getDuration() < 0) {
            throw new ValidationException("Продолжительность меньше нуля");
        }
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, " +
                "mpa_id = ? WHERE film_id = ?";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) != 0) {
            film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
            if (film.getGenres() != null) {
                Collection<Genre> sortGenres = film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toList());
                film.setGenres(new LinkedHashSet<>(sortGenres));
                for (Genre genre : film.getGenres()) {
                    genre.setName(genreService.getGenreById(genre.getId()).getName());
                }
            }
            genreService.putGenres(film);
            return film;
        } else {
            throw new NotFoundException("Фильм с ID=" + film.getId() + " не найден!");
        }
    }

    @Override
    public Map<Long, Film> getFilms() {
        return null;
    }

    @Override
    public Film getFilmById(Long filmId) throws MpaNotFoundException {
        if (filmId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        Film film;
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", filmId);
        if (filmRows.first()) {
            Mpa mpa = mpaService.getMpaById(filmRows.getInt("mpa_id"));
            Set<Genre> genres = genreService.getFilmGenres(filmId);
            film = new Film(
                    filmRows.getLong("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new HashSet<>(likeStorage.getLikes(filmRows.getLong("mpa_id"))),
                    mpa,
                    genres);
        } else {
            throw new NotFoundException("Фильм с ID=" + filmId + " не найден!");
        }
        if (film.getGenres().isEmpty()) {
            film.setGenres(null);
        }
        return film;
    }
}
