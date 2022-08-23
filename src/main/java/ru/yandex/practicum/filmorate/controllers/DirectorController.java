package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/directors")
    public List<Director> findAll() {
        List<Director> directors = directorService.findAllDirectors();
        log.debug("Текущее количество фильмов: {}", directors.size());
        return directors;
    }

    @GetMapping("/directors/{id}")
    public Director getById(@PathVariable int id) {
        log.debug("Get director by id={}", id);
        return directorService.getById(id);
    }

    @PostMapping(value = "/directors")
    public Director create(@Valid @RequestBody Director director) {
        log.debug("Количество добавленных режиссеров: {}", 1);
        directorService.create(director);
        return director;
    }

    @PutMapping(value = "/directors")
    public Director update(@Valid @RequestBody Director director) {
        log.debug("Данные режиссера обновлены");
        directorService.update(director);
        return director;
    }

    @DeleteMapping("/directors/{id}")
    public void remove(@PathVariable int id) {
        log.debug("Remove director by id={}", id);
        directorService.remove(id);
    }

}
