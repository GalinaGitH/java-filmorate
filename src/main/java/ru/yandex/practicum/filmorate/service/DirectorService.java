package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.AlreadyExistException;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> findAllDirectors() {
        return directorStorage.getAll();
    }

    public Director getById(int id) {

        return directorStorage
                .getById(id)
                .orElseThrow(() -> new NotFoundException("Director with id=" + id + "not found"));
    }

    public Director create(Director director) {

        String message = "Режиссер с таким id %s уже зарегистрирован.";
        directorStorage
                .getById(director.getId())
                .ifPresent(val -> {
                            throw new AlreadyExistException(String.format(message, val.getId()));
                        }
                );

        directorStorage.create(director);
        return director;
    }

    public Director update(Director director) {

        directorStorage
                .getById(director.getId())
                .orElseThrow(() -> new NotFoundException("Director with id=" + director.getId() + " not found"));

        directorStorage.update(director);
        return director;
    }

    public void remove(int id) {

        directorStorage.remove(
                directorStorage
                        .getById(id)
                        .orElseThrow(() -> new NotFoundException("Director with id=" + id + "not found"))
        );
    }
}
