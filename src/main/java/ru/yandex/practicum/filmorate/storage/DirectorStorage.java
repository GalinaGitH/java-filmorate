package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    Director getById(int id);

    List<Director> getAll();

    Director create(Director director);

    Director update(Director director);

    void remove(Director director);

    void setFilmDirector(Film film);

    List<Director> loadFilmDirector(Film film);

    List<Film> getSortedFilmsByYearOfDirector(int idDirector);

    List<Film> getSortedFilmsByLikesOfDirector(int idDirector);
}
