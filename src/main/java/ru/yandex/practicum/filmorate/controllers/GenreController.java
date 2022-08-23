package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class GenreController {

    private final GenreService genreService;

    /**
     * получение списка всех жанров
     */
    @GetMapping("/genres")
    public List<Genre> getAll() {
        log.debug("Общее количество жанров в справочнике : {}", genreService.getAll().size());
        return genreService.getAll();
    }

    /**
     * получение жанра по идентификатору
     */
    @GetMapping("/genres/{genreId}")
    public Genre getById(@PathVariable long genreId) {
        log.debug("Get genre by id={}", genreId);
        return genreService.getById(genreId);
    }
}
