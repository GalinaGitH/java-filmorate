package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> getById(long id);

    List<Genre> getAll();

    List<Genre> loadFilmGenre(Film film);
}
