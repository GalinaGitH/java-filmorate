package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.List;

public interface LikesStorage {

    List<Like> getLikes(long filmId, long userId);

    void addLikes(long filmId, long userId);

    void removeLikes(long filmId, long userId);

    Collection<Film> findPopularFilm(Integer size);

    Collection<Film> findPopularFilmsByYearAndGenres(Integer limit, int genreId, int year);

    Collection<Film> findPopularFilmsByYear(Integer limit, int year);

    Collection<Film> findPopularFilmsByGenre(Integer limit, int genreId);
}
