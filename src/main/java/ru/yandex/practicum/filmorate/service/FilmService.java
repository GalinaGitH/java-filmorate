package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
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
    public Collection<Film> findAllFilms() {
        Collection<Film> filmsFromStorage = filmStorage.findAll();
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
            throw new NotFoundException("User  not found");
        }
        Map<Long, HashMap<Long, Double>> idsUsersAndIdsFilms = prepareDataForSlopeOne();
        List<Film> recFilms = filmStorage.getRecommended(idsUsersAndIdsFilms, id);
        for (Film film : recFilms) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }
        return recFilms;
    }

    private Map<Long, HashMap<Long, Double>> prepareDataForSlopeOne() {
        Map<Long, HashMap<Long, Double>> preparedData = new HashMap<>();
        List<Long> idsUsers = userStorage.findAllUsers().stream().map(User::getId).collect(Collectors.toList());
        for (Long idUser : idsUsers) {
            List<Film> likedFilms = filmStorage.getLikedByUser(idUser);
            if (likedFilms != null) {
                Map<Long, Double> idsFilm = likedFilms.stream().collect(Collectors.toMap(Film::getId, val -> 1.0));
                preparedData.put(idUser, (HashMap<Long, Double>) idsFilm);
            }
        }
        return preparedData;
    }

    public Collection<Film> searchFilm(String query, List<String> by) {
        Collection<Film> filmsFromStorage = filmStorage.search(query, by);
        for (Film film : filmsFromStorage) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film)));
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }

        return filmsFromStorage;
    }

    public Collection<Film> findCommonFilms(long userId, long friendId) {
        Collection<Film> UserLikedFilms = filmStorage.getLikedByUserSortedPopular(userId);
        Collection<Film> FriendLikedFilms = filmStorage.getLikedByUserSortedPopular(friendId);
        if (UserLikedFilms.size() == 0 || FriendLikedFilms.size() == 0) {
            Collection<Film> commonFilmsEmpty = new ArrayList<>();
            return commonFilmsEmpty;
        }
        return (FriendLikedFilms.stream()
                .filter(UserLikedFilms::contains)
                .collect(Collectors.toList()));
    }

    public Collection<Film> findAllFilmsSortedByYearOrLikes(int directorId, String sortBy) {
        if ("year".equals(sortBy)) {
            checkDirector(directorId);
            List<Film> sortedFilms = directorStorage.getSortedFilmsByYearOfDirector(directorId);
            return setDirectorsAndGenresToFilms(sortedFilms);
        } else if ("likes".equals(sortBy)) {
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
