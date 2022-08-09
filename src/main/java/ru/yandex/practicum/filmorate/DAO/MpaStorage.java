package ru.yandex.practicum.filmorate.DAO;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    Mpa getById(long id);

    List<Mpa> getAll();
}
