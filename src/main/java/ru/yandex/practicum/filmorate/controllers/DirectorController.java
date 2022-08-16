package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Validated
@NoArgsConstructor
public class DirectorController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/directors")
    public Collection<Director> findAll() {
        List<Director> gotDirectors = directorService.findAllDirectors();
        log.debug("Текущее количество фильмов: {}", gotDirectors.size());
        return gotDirectors;
    }

    @GetMapping("/directors/{id}")
    public Director getDirector(@PathVariable int id) {
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
    public void removeDirector(@PathVariable int id) {
        log.debug("Remove director by id={}", id);
        directorService.remove(id);
    }

}
