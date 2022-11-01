package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class MpaRatingService {
    private MpaStorage mpaStorage;

    @Autowired
    public MpaRatingService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa().stream()
                .sorted(Comparator.comparing(Mpa::getId))
                .collect(Collectors.toList());
    }

    public Mpa getMpaById(Integer id) throws MpaNotFoundException {
        if(mpaStorage.getMpaById(id) != null) return mpaStorage.getMpaById(id);
        else throw new MpaNotFoundException("Не существующий id");
    }
}
