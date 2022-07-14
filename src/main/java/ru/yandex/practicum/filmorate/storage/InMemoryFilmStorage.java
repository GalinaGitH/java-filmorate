package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new LinkedHashMap<>();
    private long nextId = 0;

    private long getNextId() {
        return ++nextId;
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void remove(Film film) {
        films.remove(film.getId());
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film get(long filmId) {
        return films.get(filmId);
    }

}
