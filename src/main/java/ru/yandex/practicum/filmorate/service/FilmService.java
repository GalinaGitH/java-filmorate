package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class FilmService {
    InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    /**
     * добавление фильма
     */
    public Film saveFilm(Film film) {
        final Film filmFromStorage = inMemoryFilmStorage.get(film.getId());
        if (filmFromStorage == null) {
            inMemoryFilmStorage.create(film);
        } else throw new AlreadyExistException(String.format(
                "Фильм с таким id %s уже зарегистрирован.", film.getId()));
        return film;
    }

    /**
     * обновление фильма
     */
    public Film updateFilm(Film film) {
        final Film filmInStorage = inMemoryFilmStorage.get(film.getId());
        if (filmInStorage == null) {
            throw new NotFoundException("Film with id=" + film.getId() + "not found");
        }
        inMemoryFilmStorage.update(film);
        return film;
    }

    /**
     * удаление фильма
     */
    public void deleteFilm(Film film) {
        inMemoryFilmStorage.remove(film);
    }

    /**
     * получение фильма по id
     */
    public Film get(long filmId) {
        final Film film = inMemoryFilmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("User with id=" + filmId + "not found");
        }
        return film;
    }

    /**
     * получение списка всех фильмов
     */
    public Collection<Film> findAllFilms() {
        return inMemoryFilmStorage.findAll();
    }

    /**
     * добавление лайка
     */
    public void addLikes(long filmId, long userId) {
        final Film film = inMemoryFilmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film  not found");
        }
        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
        }
    }

    /**
     * удаление лайка
     */
    public void removeLikes(long filmId, long userId) {
        final Film film = inMemoryFilmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film  not found");
        }
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        } else throw new NotFoundException("Users like with id=" + userId + "not found in List of likes");
    }

    /**
     * вывод наиболее популярных фильмов по количеству лайков
     */
    public Collection<Film> findPopularFilm(Integer size) {
        Collection<Film> filmList = inMemoryFilmStorage.findAll();
        List<Film> sorted = filmList.stream().sorted(Comparator.comparing(e -> e.getLikes().size(), Comparator.reverseOrder()))
                .limit(size)
                .collect(Collectors.toList());
        return sorted;
    }

}
