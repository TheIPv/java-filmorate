package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);
    void deleteLike(Long filmId, Long userId);
    List<Film> getPopular(Integer count);
    List<Long> getLikes(Long filmId);
}
