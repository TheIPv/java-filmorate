package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Collection<Film> homeFilms() ;
    Film addFilm(Film film) throws MpaNotFoundException, GenreNotFoundException;
    Film updateFilm(Film film) throws MpaNotFoundException, GenreNotFoundException;
    Map<Long, Film> getFilms();

    Film getFilmById(Long filmId) throws MpaNotFoundException;
}
