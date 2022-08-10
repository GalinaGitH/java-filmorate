package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesStorage likesStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    /**
     * добавление лайка
     */
    public void addLikes(long filmId, long userId) {
        final Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film  not found");
        }
        likesStorage.addLikes(filmId, userId);
    }

    /**
     * удаление лайка
     */
    public void removeLikes(long filmId, long userId) {
        final Film film = filmStorage.get(filmId);
        final User user = userStorage.get(userId);
        if (film == null || user == null) {
            throw new NotFoundException("Film or User  not found");
        }
        likesStorage.removeLikes(filmId, userId);
    }

    /**
     * вывод наиболее популярных фильмов по количеству лайков
     */
    public Collection<Film> findPopularFilm(Integer size) {
        return likesStorage.findPopularFilm(size);
    }

}
