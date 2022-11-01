package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.stream.Stream;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Stream<Film> getMostPopularFilms(Integer count) {
        if(count == null) count = 10;
          return filmStorage.homeFilms().stream()
                  .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                  .limit(count);
    }
    public void addLike(Film film, User user) {
        film.addLike(user);
    }
    public void removeLike(Film film, User user) {
        film.removeLike(user);
    }
}
