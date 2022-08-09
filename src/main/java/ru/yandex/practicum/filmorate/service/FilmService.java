package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.DAO.GenreStorage;
import ru.yandex.practicum.filmorate.DAO.MpaStorage;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    /**
     * добавление фильма
     */
    public Film saveFilm(Film film) {
        final Film filmFromStorage = filmStorage.get(film.getId());
        if (filmFromStorage == null) {
            filmStorage.create(film);
        } else throw new AlreadyExistException(String.format(
                "Фильм с таким id %s уже зарегистрирован.", film.getId()));
        return film;
    }

    /**
     * обновление фильма
     */
    public Film updateFilm(Film film) {
        final Film filmInStorage = filmStorage.get(film.getId());
        if (filmInStorage == null) {
            throw new NotFoundException("Film with id=" + film.getId() + "not found");
        }
        filmStorage.update(film);
        return film;
    }

    /**
     * удаление фильма
     */
    public void deleteFilm(Film film) {
        filmStorage.remove(film);
    }

    /**
     * получение фильма по id
     */
    public Film get(long filmId) {
        final Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("User with id=" + filmId + "not found");
        }
        return film;
    }

    /**
     * получение списка всех фильмов
     */
    public Collection<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    /**
     * получение жанра по идентификатору
     */
    public Genre getGenreById(long id) {
        if (id > 6 || id < 1) {
            throw new NotFoundException("Genre with id=" + id + "not found");
        }
        return genreStorage.getById(id);
    }

    /**
     * получение списка всех жанров
     */
    public List<Genre> getAllGenres() {
        return genreStorage.getAll();
    }

    /**
     * получение рейтинга(MPA) по идентификатору
     */
    public Mpa getMPAById(long id) {
        if (id > 5 || id < 1) {
            throw new NotFoundException("MPA with id=" + id + "not found");
        }
        return mpaStorage.getById(id);
    }

    /**
     * получение списка всех рейтингов(MPA)
     */
    public List<Mpa> getAllMPA() {
        return mpaStorage.getAll();
    }

}
