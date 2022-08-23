package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;

    private final UserStorage userStorage;

    /**
     * добавление фильма
     */
    public Film saveFilm(Film film) {
        final Film filmFromStorage = filmStorage.get(film.getId());
        if (filmFromStorage == null) {
            filmStorage.create(film);
            genreStorage.setFilmGenre(film);
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
        genreStorage.setFilmGenre(film);
        directorStorage.setFilmDirector(film);
        if (film.getDirectors().isEmpty()) {
            film.setDirectors(null);
        }
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
        film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
        film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        return film;
    }

    /**
     * получение списка всех фильмов
     */
    public List<Film> findAllFilms() {
        List<Film> filmsFromStorage = filmStorage.findAll();
        for (Film film : filmsFromStorage) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }

        return filmsFromStorage;
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

    public List<Film> getRecommended(long id) {
        final User user = userStorage.get(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        List<Film> recFilms = filmStorage.getRecommended(id);
        for (Film film : recFilms) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }
        return recFilms;
    }

    public List<Film> searchFilm(String query, List<String> by) {
        List<Film> filmsFromStorage = filmStorage.search(query, by);
        for (Film film : filmsFromStorage) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }

        return filmsFromStorage;
    }

    public List<Film> findCommonFilms(long userId, long friendId) {
        List<Film> UserLikedFilms = filmStorage.getLikedByUserSortedPopular(userId);
        List<Film> FriendLikedFilms = filmStorage.getLikedByUserSortedPopular(friendId);
        if (UserLikedFilms.size() == 0 || FriendLikedFilms.size() == 0) {
            List<Film> commonFilmsEmpty = new ArrayList<>();
            return commonFilmsEmpty;
        }
        return (FriendLikedFilms.stream()
                .filter(UserLikedFilms::contains)
                .collect(Collectors.toList()));
    }

    public List<Film> findAllFilmsSortedByYearOrLikes(int directorId, FilmSortBy sortBy) {

        if (FilmSortBy.year == sortBy) {
            checkDirector(directorId);
            List<Film> sortedFilms = directorStorage.getSortedFilmsByYearOfDirector(directorId);
            return setDirectorsAndGenresToFilms(sortedFilms);
        } else if (FilmSortBy.likes == sortBy) {
            checkDirector(directorId);
            List<Film> sortedFilms = directorStorage.getSortedFilmsByLikesOfDirector(directorId);
            return setDirectorsAndGenresToFilms(sortedFilms);
        } else {
            return null;
        }
    }

    private void checkDirector(int idDirector) {
        final Director directorFromStorage = directorStorage.getById(idDirector);
        if (directorFromStorage == null) {
            throw new NotFoundException("Director with id=" + idDirector + "not found");
        }
    }

    private List<Film> setDirectorsAndGenresToFilms(List<Film> filmsFromStorage) {
        for (Film film : filmsFromStorage) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }
        return filmsFromStorage;
    }
}
