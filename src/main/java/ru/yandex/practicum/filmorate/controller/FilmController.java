package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895,Month.DECEMBER, 28);
    int uniqueId = 1;
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public String homeFilms() { return films.values().toString(); }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if(film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Дата выхода фильма не может быть раньше, чем "
                    + EARLIEST_DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            );
        }
        if(film.getId() != 0) {
            throw new ValidationException("Вы пытаетесь добавить существующий фильм");
        }
        film.setId(uniqueId);
        films.put(uniqueId, film); ++uniqueId;
        return film;
    }
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if(film.getId() <= 0) {
            throw new ValidationException("Вы пытаетесь изменить несуществующий фильм");
        }
        if(film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Дата выхода фильма не может быть раньше, чем "
                    + EARLIEST_DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
        }
        films.put(film.getId(), film);
        return film;
    }
}
