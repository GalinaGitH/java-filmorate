package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    void remove(Film film);

    Film get(long filmId);

    List<Film> findAll();

    void removeFilmById(long filmId);

    List<Film> search(String query, List<String> by);

    List<Film> getLikedByUser(long userId);

    List<Film> getLikedByUserSortedPopular(long userId);

    List<Film> getRecommended(long id);

}
