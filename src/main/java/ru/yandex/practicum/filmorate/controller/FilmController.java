package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895,Month.DECEMBER, 28);
    int uniqueId = 1;
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> homeFilms() { return films.values(); }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
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
        if(film.getId() != 0) {
            throw new ValidationException("Вы пытаетесь добавить существующий фильм");
        }
        if(film.getDuration() < 0) {
            throw new ValidationException("Продолжительность меньше нуля");
        }
        film.setId(uniqueId);
        films.put(uniqueId, film); ++uniqueId;
        return film;
    }
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if(film.getId() <= 0) {
            throw new NotFoundException("Вы пытаетесь изменить несуществующий фильм");
        }
        if(!Objects.isNull(film.getDescription())) {
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Описание фильма не может превышать 200 символов");
            }
        }
        if(film.getName().equals("")) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if(film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Дата выхода фильма не может быть раньше, чем "
                    + EARLIEST_DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
        }
        if(film.getDuration() < 0) {
            throw new ValidationException("Продолжительность меньше нуля");
        }
        films.put(film.getId(), film);
        return film;
    }
}
