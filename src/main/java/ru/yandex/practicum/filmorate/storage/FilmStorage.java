package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;


public interface FilmStorage {
    
    Film create(Film film);

    Film update(Film film);

    void remove(Film film);

    Optional<Film> get(long filmId);

    List<Film> findAll();

    void removeFilmById(long filmId);

    List<Film> search(String query, List<String> by);

    List<Film> getLikedByUserSortedPopular(long userId);

    List<Film> findByIds(List<Long> recIdsFilms);

}
