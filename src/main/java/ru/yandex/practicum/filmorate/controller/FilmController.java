package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, UserStorage userStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmService = filmService;
    }

    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> homeFilms() {
        return filmStorage.homeFilms(); }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmStorage.addFilm(film);
    }
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        if(filmStorage.getFilms().containsKey(id)) return filmStorage.getFilms().get(id);
        else throw new NotFoundException("Фильм не найден");
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        if(userStorage.getUsers().get(userId) != null) filmStorage.getFilms().get(id).addLike(userStorage.getUsers().get(userId));
        else throw new NotFoundException("Лайк уже поставлен");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        if(userStorage.getUsers().get(userId) != null) filmStorage.getFilms().get(id).removeLike(userStorage.getUsers().get(userId));
        else throw new NotFoundException("Лайк не найден");
    }

    @GetMapping("/popular")
    public Stream<Film> getPopularFilms(@RequestParam @Nullable Integer count) {
        return filmService.getMostPopularFilms(count);
    }

}
