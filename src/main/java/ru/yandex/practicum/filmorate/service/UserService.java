package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(int firstUserId, int secondUserId) {
        userStorage.getUsers().get(firstUserId).addFriend(secondUserId);
        userStorage.getUsers().get(secondUserId).addFriend(firstUserId);
        return userStorage.getUsers().get(firstUserId);
    }

    public User removeFriend(int firstUserId, int secondUserId) {
        userStorage.getUsers().get(firstUserId).removeFriend(secondUserId);
        userStorage.getUsers().get(secondUserId).removeFriend(firstUserId);
        return userStorage.getUsers().get(firstUserId);
    }
}
