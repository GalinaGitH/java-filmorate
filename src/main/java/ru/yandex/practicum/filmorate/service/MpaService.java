package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    /**
     * получение рейтинга(MPA) по идентификатору
     */
    public Mpa getById(long id) {
        Mpa mpa = mpaStorage.getById(id);
        if (mpa == null) {
            throw new NotFoundException("MPA with id=" + id + "not found");
        }
        return mpa;
    }

    /**
     * получение списка всех рейтингов(MPA)
     */
    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }
}
