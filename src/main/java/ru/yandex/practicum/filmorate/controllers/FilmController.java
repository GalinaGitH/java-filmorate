package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikesService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

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

    @GetMapping("/films/search")
    public Collection<Film> searchFilm(@RequestParam String query, @RequestParam List<String> by) {
        log.debug("Поиск по фильмам {}, {}", query, by);
        return filmService.searchFilm(query, by);
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> sortByFilm(@PathVariable int directorId, @RequestParam String sortBy) {
        log.debug("Сортируем список фильмов конкретного режиссера с id={} по годам", directorId);
        return filmService.findAllFilmsSortedByYearOrLikes(directorId, sortBy);
    }

    @DeleteMapping("/films/{filmId}")
    public void deleteFilmById(@PathVariable long filmId) {
        filmService.deleteFilmById(filmId);
        log.debug("Фильм с id = {} удален из списка", filmId);
    }

    @GetMapping("/films/common")
    public Collection<Film> findCommonFilms(@RequestParam String userId, @RequestParam String friendId) {
        log.debug("Список общих фильмов пользователя с Id={} и пользователя с id={} по популярности", userId, friendId);
        return filmService.findCommonFilms(Long.parseLong(userId), Long.parseLong(friendId));
    }

    @GetMapping("/films/popular")
    public Collection<Film> findPopularFilmsByGenresAndYear(@RequestParam(defaultValue = "10", required = false) @Positive Integer count, @RequestParam(required = false) String year, @RequestParam(required = false) String genreId) {
        log.debug("Список из {} самых популярных фильмов по жанру с Id={} и году {}", count, year, genreId);

        return likesService.findPopularFilmsByGenresAndYear(count, year, genreId);
    }

}


