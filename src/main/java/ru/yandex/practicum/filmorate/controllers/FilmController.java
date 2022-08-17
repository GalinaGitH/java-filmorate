package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikesService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@RestController
@Validated
@NoArgsConstructor
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private FilmService filmService;
    private LikesService likesService;

    @Autowired
    public FilmController(FilmService filmService, LikesService likesService) {
        this.filmService = filmService;
        this.likesService = likesService;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", filmService.findAllFilms().size());
        return filmService.findAllFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable long filmId) {
        log.debug("Get film by id={}", filmId);
        return filmService.get(filmId);
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Количество добавленных фильмов: {}", 1);
        filmService.saveFilm(film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Данные фильма обновлены");
        filmService.updateFilm(film);
        return film;
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public void addLikes(@PathVariable long filmId, @PathVariable long userId) {
        log.debug("Добавлен еще один лайк фильму: {} от пользователя c id = {}",
                filmService.get(filmId).getName(),
                userId);
        likesService.addLikes(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public void deleteLikes(@PathVariable long filmId, @PathVariable long userId) {
        log.debug("Лайк пользователя с id = {} удален из списка", userId);
        likesService.removeLikes(filmId, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> findPopularFilm(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.debug("Список из {} самых популярных фильмов", count);
        return likesService.findPopularFilm(count);
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> sortByFilm(@PathVariable int directorId, @RequestParam String sortBy) {
        if ("year".equals(sortBy)) {
            log.debug("Сортируем список фильмов конкретного режиссера с id={} по годам", directorId);
            return filmService.findAllFilmsOfDirectorSortedByYear(directorId);
        } else if ("likes".equals(sortBy)) {
            log.debug("Сортируем список фильмов конкретного режиссера с id={} по лайкам", directorId);
            return filmService.findAllFilmsOfDirectorSortedByLikes(directorId);
        } else {
            log.debug("Ошибка в параметре {}", sortBy);
            return null;
        }
    }

    @DeleteMapping("/films/{filmId}")
    public void deleteFilmById(@PathVariable long filmId) {
        filmService.deleteFilmById(filmId);
        log.debug("Фильм с id = {} удален из списка", filmId);
    }

}


