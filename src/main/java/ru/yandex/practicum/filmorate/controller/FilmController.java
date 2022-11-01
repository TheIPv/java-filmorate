package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.likeStorage = likeStorage;
    }

    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> homeFilms() {
        return filmStorage.homeFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws MpaNotFoundException, GenreNotFoundException {
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws MpaNotFoundException, GenreNotFoundException {
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) throws MpaNotFoundException {
        if (filmStorage.getFilmById(id) != null) return filmStorage.getFilmById(id);
        else throw new NotFoundException("The movie was not found");
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        if (id != null && userId != null && !likeStorage.getLikes(id).contains(userId)) likeStorage.addLike(id, userId);
        else throw new NotFoundException("The movie/user was not found or like has already been set");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        if (id != null && userId != null && likeStorage.getLikes(id).contains(userId))
            likeStorage.deleteLike(id, userId);
        else throw new NotFoundException("The movie/user/like was not found");
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam @Nullable Integer count) {
        return filmService.getPopular(count);
    }

}
