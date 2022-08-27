package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.AlreadyExistException;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.service.recommendation.RecommendationService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;
    private final RecommendationService recommendationService;
    private final LikesStorage likesStorage;

    /**
     * добавление фильма
     */
    public Film saveFilm(Film film) {
        String message = "Фильм с таким id %s уже зарегистрирован.";
        filmStorage
                .get(film.getId())
                .ifPresent(val -> {
                            throw new AlreadyExistException(String.format(message, film.getId()));
                        }
                );

        filmStorage.create(film);
        directorStorage.setFilmDirector(film);
        return film;
    }

    /**
     * обновление фильма
     */
    public Film updateFilm(Film film) {

        filmStorage
                .get(film.getId())
                .orElseThrow(() -> new NotFoundException("Film with id=" + film.getId() + "not found"));

        filmStorage.update(film);
        directorStorage.setFilmDirector(film);
        if (film.getDirectors().isEmpty()) {
            film.setDirectors(null);
        }
        return film;
    }

    /**
     * получение фильма по id
     */
    public Film get(long filmId) {

        Film film = filmStorage
                .get(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + "not found"));

        film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        return film;
    }

    /**
     * получение списка всех фильмов
     */
    public List<Film> findAllFilms() {
        List<Film> filmsFromStorage = filmStorage.findAll();
        for (Film film : filmsFromStorage) {
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }

        return filmsFromStorage;
    }

    /**
     * удаление фильма по Id
     */
    public void deleteFilmById(long filmId) {

        filmStorage
                .get(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + "not found"));

        filmStorage.removeFilmById(filmId);
    }

    public List<Film> getRecommended(long id) {
        userStorage
                .get(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Map<Long, HashMap<Long, Double>> idsUsersAndIdsFilms = prepareUsersFilmsForRecommendationService();
        recommendationService.setUsersItemsMap(idsUsersAndIdsFilms);
        List<Long> recIdsFilms = recommendationService.getRecommendedIdsItemForUser(id);
        List<Film> recFilms = filmStorage.findByIds(recIdsFilms);
        for (Film film : recFilms) {
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }
        return recFilms;
    }

    private Map<Long, HashMap<Long, Double>> prepareUsersFilmsForRecommendationService() {
        List<Like> likesBD = likesStorage.getTopLikes(100000);

        Map<Long, HashMap<Long, Double>> preparedData = new HashMap<>();
        List<Long> idsUsers = likesBD
                .stream()
                .map(Like::getUser_id)
                .collect(Collectors.toList());

        for (Long idUser : idsUsers) {
            Map<Long, Double> filmsScore = likesBD
                    .stream()
                    .filter(val -> val.getUser_id().equals(idUser))
                    .collect(Collectors.toMap(Like::getFilm_id, val -> val.getScore() * 1.0));

            preparedData.put(idUser, (HashMap<Long, Double>) filmsScore);
        }
        return preparedData;
    }

    public List<Film> searchFilm(String query, List<String> by) {
        List<Film> filmsFromStorage = filmStorage.search(query, by);
        for (Film film : filmsFromStorage) {
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }

        return filmsFromStorage;
    }

    public List<Film> findCommonFilms(long userId, long friendId) {

        userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + "not found"));

        userStorage
                .get(friendId)
                .orElseThrow(() -> new NotFoundException("User with id=" + friendId+ "not found"));

        return filmStorage.findCommonFilms(userId, friendId);
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

        directorStorage
                .getById(idDirector)
                .orElseThrow(() -> new NotFoundException("Director with id=" + idDirector + "not found"));
    }

    private List<Film> setDirectorsAndGenresToFilms(List<Film> filmsFromStorage) {
        for (Film film : filmsFromStorage) {
            film.setDirectors(new HashSet<>(directorStorage.loadFilmDirector(film)));
        }
        return filmsFromStorage;
    }
}
