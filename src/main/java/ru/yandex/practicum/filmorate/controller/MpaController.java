package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    private MpaRatingService mpaService;
    @Autowired
    public MpaController(MpaRatingService mpaService) {
        this.mpaService = mpaService;
    }
    @GetMapping
    public Collection<Mpa> homeMpa() {
        return mpaService.getAllMpa(); }
    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable Integer id) throws MpaNotFoundException {
        return mpaService.getMpaById(id);
    }
}
