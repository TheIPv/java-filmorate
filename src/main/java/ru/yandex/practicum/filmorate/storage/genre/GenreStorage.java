package ru.yandex.practicum.filmorate.storage.genre;

import java.util.List;

import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {

    List<Genre> getGenres();
    Genre getGenreById(Integer genreId) throws GenreNotFoundException;
    void delete(Film film);
    void add(Film film);
    List<Genre> getFilmGenres(Long filmId);
}
