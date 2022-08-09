package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface LikesStorage {
    void addLikes(long filmId, long userId);

    void removeLikes(long filmId, long userId);

    Collection<Film> findPopularFilm(Integer size);
}
