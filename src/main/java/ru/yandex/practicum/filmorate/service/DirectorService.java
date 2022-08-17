package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        final Director directorById = directorStorage.getById(id);
        if (directorById == null) {
            throw new NotFoundException("Director with id=" + id + "not found");
        }
        return directorById;
    }

    public Director create(Director director) {
        final Director directorFromStorage = directorStorage.getById(director.getId());
        if (directorFromStorage == null) {
            directorStorage.create(director);
        } else throw new AlreadyExistException(String.format(
                "Режиссер с таким id %s уже зарегистрирован.", director.getId()));
        return director;
    }

    public Director update(Director director) {
        final Director directorFromStorage = directorStorage.getById(director.getId());
        if (directorFromStorage == null) {
            throw new NotFoundException("Director with id=" + director.getId() + "not found");
        }
        directorStorage.update(director);
        return director;
    }

    public void remove(int id) {
        final Director directorFromStorage = directorStorage.getById(id);
        if (directorFromStorage == null) {
            throw new NotFoundException("Director with id=" + id + "not found");
        }
        directorStorage.remove(directorFromStorage);
    }
}
