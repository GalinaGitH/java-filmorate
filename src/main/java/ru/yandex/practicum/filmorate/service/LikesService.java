package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesStorage likesStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;

    private final FeedService feedService;

    /**
     * добавление лайка
     */
    public void addOrUpdateLikes(long filmId, long userId, Integer score) {

        filmStorage
                .get(filmId)
                .orElseThrow(() -> new NotFoundException("Film  not found"));

        if (likesStorage.getLikes(filmId, userId).size() == 0) {
            feedService.addLikeFilmInFeed(filmId, userId);
            likesStorage.addLikes(filmId, userId, score);
        } else {
            feedService.addLikeFilmInFeed(filmId, userId);
            likesStorage.updateLikes(filmId, userId, score);
        }
    }

    /**
     * удаление лайка
     */
    public void removeLikes(long filmId, long userId) {

        filmStorage
                .get(filmId)
                .orElseThrow(() -> new NotFoundException("Film or User not found"));

        userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("Film or User not found"));

        feedService.removeLikeFilmInFeed(filmId, userId);
        likesStorage.removeLikes(filmId, userId);
    }

    /**
     * вывод наиболее популярных фильмов по количеству лайков
     */
    public List<Film> findPopularFilmsByGenresAndYear(Integer limit, String year, String genreId) {

        if (genreId == null && year == null) {
            return setGenresToFilms(likesStorage.findPopularFilm(limit));
        }
        if (genreId == null && year != null) {
            return setGenresToFilms(likesStorage.findPopularFilmsByYear(limit, Integer.parseInt(year)));
        }
        if (genreId != null && year == null) {
            return setGenresToFilms(likesStorage.findPopularFilmsByGenre(limit, Integer.parseInt(genreId)));
        }

        List<Film> popularFilms = likesStorage.findPopularFilmsByYearAndGenres(limit, Integer.parseInt(year),
                Integer.parseInt(genreId));
        return setGenresToFilms(popularFilms);
    }

    private List<Film> setGenresToFilms(List<Film> films) {
        for (Film film : films) {
            film.setGenres(new HashSet<>(genreStorage.loadFilmGenre(film))); //получаем жанры фильма,добавляем к обьекту
        }
        return films;
    }
}
