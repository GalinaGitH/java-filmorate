package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RestController
@Validated
@NoArgsConstructor
public class MpaController {
    private static final Logger log = LoggerFactory.getLogger(MpaController.class);

    private MpaStorage mpaStorage;

    @Autowired
    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    /**
     * получение рейтинга(MPA) по идентификатору
     */
    @GetMapping("/mpa/{id}")
    public Mpa getMPAById(@PathVariable long id) {
        if (id > 5 || id < 1) {
            throw new NotFoundException("MPA with id=" + id + "not found");
        }
        log.debug("Get MPA by id={}", id);
        return mpaStorage.getById(id);
    }

    /**
     * получение списка всех рейтингов(MPA)
     */
    @GetMapping("/mpa")
    public List<Mpa> getAllMPA() {
        log.debug("Общее количество рейтингов MPA : {}", mpaStorage.getAll().size());
        return mpaStorage.getAll();
    }

}
