package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikesService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final LikesService likesService;

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        log.debug("Текущее количество фильмов: {}", filmService.findAllFilms().size());
        return filmService.findAllFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable long filmId) {
        log.debug("Get film by id={}", filmId);
        return filmService.get(filmId);
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Количество добавленных фильмов: {}", 1);
        filmService.saveFilm(film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
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
    public List<Film> searchFilm(@RequestParam String query, @RequestParam List<String> by) {
        log.debug("Поиск по фильмам {}, {}", query, by);
        return filmService.searchFilm(query, by);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> sortByFilm(@PathVariable int directorId, @RequestParam FilmSortBy sortBy) {
        log.debug("Сортируем список фильмов конкретного режиссера с id={} по годам", directorId);
        return filmService.findAllFilmsSortedByYearOrLikes(directorId, sortBy);
    }

    @DeleteMapping("/films/{filmId}")
    public void deleteFilmById(@PathVariable long filmId) {
        filmService.deleteFilmById(filmId);
        log.debug("Фильм с id = {} удален из списка", filmId);
    }

    @GetMapping("/films/common")
    public List<Film> findCommonFilms(@RequestParam String userId, @RequestParam String friendId) {
        log.debug("Список общих фильмов пользователя с Id={} и пользователя с id={} по популярности", userId, friendId);
        return filmService.findCommonFilms(Long.parseLong(userId), Long.parseLong(friendId));
    }

    @GetMapping("/films/popular")
    public List<Film> findPopularFilmsByGenresAndYear(@RequestParam(defaultValue = "10", required = false) @Positive Integer count, @RequestParam(required = false) String year, @RequestParam(required = false) String genreId) {
        log.debug("Список из {} самых популярных фильмов по жанру с Id={} и году {}", count, year, genreId);

        return likesService.findPopularFilmsByGenresAndYear(count, year, genreId);
    }

}


