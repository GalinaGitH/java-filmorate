package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

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
     * добавление лайка
     */
    public void addLikes(long filmId, long userId) {
        final Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film  not found");
        }
        if (!film.getLikes().contains(userId)) { //оставила проверку на случай будущих изменений, выведения сообщения
            film.getLikes().add(userId);
        }
    }

    /**
     * удаление лайка
     */
    public void removeLikes(long filmId, long userId) {
        final Film film = filmStorage.get(filmId);
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
        Collection<Film> filmList = filmStorage.findAll();
        List<Film> sorted = filmList.stream().sorted(Comparator.comparing(e -> e.getLikes().size(), Comparator.reverseOrder()))
                .limit(size)
                .collect(Collectors.toList());
        return sorted;
    }

}
