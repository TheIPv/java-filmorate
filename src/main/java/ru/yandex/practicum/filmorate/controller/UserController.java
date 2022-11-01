package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
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
    private final FriendStorage friendStorage;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.userService = userService;
        this.friendStorage = friendStorage;
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
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (userStorage.getUserById(id) != null && userStorage.getUserById(friendId) != null) {
            return userService.addFriend(id, friendId);
        }
        throw new NotFoundException("Пользователь не найден");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if(userStorage.getUserById(id) == null || userStorage.getUserById(friendId) == null ) throw new NotFoundException("Пользователь не найден");
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        if(userStorage.getUserById(id) != null) return userStorage.getUserById(id);
        else throw new NotFoundException("Не найдено");
    }
    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) {
        return friendStorage.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) throws NotFoundException {
        Set<User> commonFriends = new HashSet<>();
        if(userStorage.getUserById(id) != null && friendStorage.getFriends(id) != null) {
            for (User friendId : friendStorage.getFriends(id)) {
                if (friendStorage.getFriends(otherId).contains(friendId)) commonFriends.add(friendId);
            }
        }
        return commonFriends;
    }

}
