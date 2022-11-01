package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Collection<Film> homeFilms();
    Film addFilm(Film film);
    Film updateFilm(Film film);
    Map<Integer, Film> getFilms();

}
