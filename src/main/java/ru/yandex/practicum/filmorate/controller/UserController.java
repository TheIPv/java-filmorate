package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    int uniqueId = 1;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public String homeFilms() { return users.values().toString(); }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if(user.getId() != 0) {
            throw new ValidationException("Вы пытаетесь добавить существующего пользователя");
        }
        if(!user.getEmail().contains("@") || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("Ошибка при вводе электронной почты");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if(Objects.isNull(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(uniqueId);
        users.put(uniqueId, user);
        ++uniqueId;
        return user;
    }
    @PutMapping
    public User updateUser(@RequestBody User user) {
        if(user.getId() >= uniqueId || user.getId() <= 0) {
            throw new ValidationException("Вы пытаетесь изменить несуществующего пользователя");
        }
        if(!user.getEmail().contains("@") || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("Ошибка при вводе электронной почты");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if(user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }
}
