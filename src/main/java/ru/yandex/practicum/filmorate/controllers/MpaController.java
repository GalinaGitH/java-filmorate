package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    /**
     * получение рейтинга(MPA) по идентификатору
     */
    @GetMapping("/mpa/{id}")
    public Mpa getById(@PathVariable long id) {
        Mpa mpa = mpaService.getById(id);
        log.debug("Get MPA by id={}", id);
        return mpa;
    }

    /**
     * получение списка всех рейтингов(MPA)
     */
    @GetMapping("/mpa")
    public List<Mpa> getAll() {
        log.debug("Общее количество рейтингов MPA : {}", mpaService.getAll().size());
        return mpaService.getAll();
    }

}
