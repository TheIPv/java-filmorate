package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    int uniqueId = 1;
    private Map<Integer, User> users = new HashMap<>();


    public Collection<User> homeUsers() { return users.values(); }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }


    public User addUser(User user) {
        if(user.getId() != 0) {
            throw new ValidationException("Вы пытаетесь добавить существующего пользователя");
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
        user.setId(uniqueId);
        users.put(uniqueId, user);
        ++uniqueId;
        return user;
    }

    public User updateUser(User user) {
        if(user.getId() >= uniqueId || user.getId() <= 0) {
            throw new NotFoundException("Вы пытаетесь изменить несуществующего пользователя");
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
