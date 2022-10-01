package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }
    @GetMapping
    public Collection<User> homeUsers() { return userStorage.homeUsers(); }

    @PostMapping
    public User addUser(@RequestBody User user) {

      return userStorage.addUser(user);
    }
    @PutMapping
    public User updateUser(@RequestBody User user) {
        if(user.getName().isEmpty()) user.setName(user.getLogin());
       return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        if (userStorage.getUsers().get(id) != null && userStorage.getUsers().get(friendId) != null) {
            return userService.addFriend(id, friendId);

        }
        throw new NotFoundException("Пользователь не найден");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable int id, @PathVariable int friendId) {
        if(userStorage.getUsers().get(id) == null || userStorage.getUsers().get(friendId) == null ) throw new NotFoundException("Пользователь не найден");
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        if(userStorage.getUsers().containsKey(id)) return userStorage.getUsers().get(id);
        else throw new NotFoundException("Пользователь не найден");
    }
    @GetMapping("/{id}/friends")
    public Set<User> getUserFriends(@PathVariable int id) {
        Set<User> userFriends = new HashSet<>();
        for (int friendId : userStorage.getUsers().get(id).getFriends()) {
            userFriends.add(userStorage.getUsers().get(friendId));
        }
        return userFriends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) throws NotFoundException {
        Set<User> commonFriends = new HashSet<>();
        if(userStorage.getUsers().get(id) != null && userStorage.getUsers().get(id).getFriends() != null) {
            for (int friendId : userStorage.getUsers().get(id).getFriends()) {
                if (userStorage.getUsers().get(otherId).getFriends().contains(friendId)) commonFriends.add(userStorage.getUsers().get(friendId));
            }
        }


        return commonFriends;
    }

}
