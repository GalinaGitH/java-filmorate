package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    void remove(Film film);

    Film get(long filmId);

    Collection<Film> findAll();

    void removeFilmById(long filmId);

    Collection<Film> search(String query, List<String> by);

    List<Film> getLikedByUser(long userId);

    Collection<Film> getLikedByUserSortedPopular(long userId);

    List<Film> getRecommended(Map<Long, HashMap<Long, Double>> idsUsersAndIdsFilms, long id);

    List<Film> getFilmsFromIds(List <Long> idFilms);
}
