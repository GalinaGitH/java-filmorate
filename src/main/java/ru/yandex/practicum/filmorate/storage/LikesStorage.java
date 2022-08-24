package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikesStorage {

    List<Like> getLikes(long filmId, long userId);

    void addLikes(long filmId, long userId, Integer score);

    void removeLikes(long filmId, long userId);

    List<Film> findPopularFilm(Integer size);

    List<Film> findPopularFilmsByYearAndGenres(Integer limit, int genreId, int year);

    List<Film> findPopularFilmsByYear(Integer limit, int year);

    List<Film> findPopularFilmsByGenre(Integer limit, int genreId);

    void updateLikes(long filmId, long userId, Integer score);
}
