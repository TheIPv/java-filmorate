package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
    private Set<User> likes = new HashSet<>();

    public void addLike(User user) {
        likes.add(user);
    }
    public void removeLike(User user) {
        likes.remove(user);
    }

}
