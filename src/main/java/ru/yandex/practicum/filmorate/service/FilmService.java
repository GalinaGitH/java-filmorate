package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;

    /**
     * добавление фильма
     */
    public Film saveFilm(Film film) {
        final Film filmFromStorage = filmStorage.get(film.getId());
        if (filmFromStorage == null) {
            filmStorage.create(film);
            genreStorage.setFilmGenre(film);//записываем жанры фильму,заполняем таблицу FILM_GENRES
            directorStorage.setFilmDirector(film);
        } else throw new AlreadyExistException(String.format(
                "Фильм с таким id %s уже зарегистрирован.", film.getId()));
        return film;
    }

    /**
     * обновление фильма
     */
    public Film updateFilm(Film film) {
        final Film filmInStorage = filmStorage.get(film.getId());
        if (filmInStorage == null) {
            throw new NotFoundException("Film with id=" + film.getId() + "not found");
        }
        filmStorage.update(film);
        genreStorage.setFilmGenre(film);//записываем жанры фильму,заполняем таблицу FILM_GENRES
        directorStorage.setFilmDirector(film);
        return film;
    }

    /**
     * удаление фильма
     */
    public void deleteFilm(Film film) {
        filmStorage.remove(film);
    }

    /**
     * получение фильма по id
     */
    public Film get(long filmId) {
        final Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film with id=" + filmId + "not found");
        }
        film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film))); //получаем жанры фильма и добавляем к обьекту
        film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        return film;
    }

    /**
     * получение списка всех фильмов
     */
    public Collection<Film> findAllFilms() {
        Collection<Film> filmsFromStorage = filmStorage.findAll();
        for (Film film : filmsFromStorage) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }

        return filmsFromStorage;
    }

    public Collection<Film> findAllFilmsOfDirectorSortedByYear(int idDirector) {
        final Director directorFromStorage = directorStorage.getById(idDirector);
        if (directorFromStorage == null) {
            throw new NotFoundException("Director with id=" + idDirector + "not found");
        }
        List<Film> sortedFilms = directorStorage.getSortedFilmsByYearOfDirector(idDirector);
        for (Film film : sortedFilms) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }
        return sortedFilms;
    }

    public Collection<Film> findAllFilmsOfDirectorSortedByLikes(int idDirector) {
        final Director directorFromStorage = directorStorage.getById(idDirector);
        if (directorFromStorage == null) {
            throw new NotFoundException("Director with id=" + idDirector + "not found");
        }
        List<Film> sortedFilms = directorStorage.getSortedFilmsByLikesOfDirector(idDirector);
        for (Film film : sortedFilms) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }
        return sortedFilms;
    }

    /**
     * удаление фильма по Id
     */
    public void deleteFilmById(long filmId) {
        final Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film with id=" + filmId + "not found");
        }
        filmStorage.removeFilmById(filmId);
    }


    /**
     * удаление фильма по Id
     */
    public void deleteFilmById(long filmId) {
        final Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film with id=" + filmId + "not found");
        }
        filmStorage.removeFilmById(filmId);
    }

}
