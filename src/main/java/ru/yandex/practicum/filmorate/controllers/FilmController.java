package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@RestController
@Validated
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private int nextId = 0;

    private int getNextId() {
        return ++nextId;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody @Valid Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("количество добавленных фильмов: {}", 1);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            RuntimeException e = new ValidationException("Фильм с таким Id не найден");
            log.error(e.getMessage());
            throw e;
        }
        films.put(film.getId(), film);
        log.debug("Данные фильма обновлены");
        return film;
    }

}


