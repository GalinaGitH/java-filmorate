package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getById(long id);

    List<Genre> getAll();

    void setFilmGenre(Film film);

    List<Genre> loadFilmGenre(Film film);
}
