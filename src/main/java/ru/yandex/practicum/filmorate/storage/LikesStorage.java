package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Optional;

public interface LikesStorage {

    Optional<Like> getLike(long filmId, long userId);

    List<Like> getTopLikes(int limit);

    void addLike(long filmId, long userId, Integer score);

    void removeLike(long filmId, long userId);

    List<Film> findPopularFilm(Integer size);

    List<Film> findPopularFilmsByYearAndGenres(Integer limit, int genreId, int year);

    List<Film> findPopularFilmsByYear(Integer limit, int year);

    List<Film> findPopularFilmsByGenre(Integer limit, int genreId);

    void updateLike(long filmId, long userId, Integer score);
}
