package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final FeedService feedService;

    /**
     * сохранение отзыва
     */
    public Review saveReview(Review review) {
        final User userInStorage = userStorage.get(review.getUserId());
        final Film film = filmStorage.get(review.getFilmId());
        checkUser(userInStorage);
        checkFilm(film);
        final Review reviewFromStorage = reviewStorage.get(review.getReviewId());
        if (reviewFromStorage == null) {
            Review newReview = reviewStorage.create(review);
            feedService.addReviewFilmInFeed(newReview.getUserId(), newReview.getReviewId());
            return newReview;
        } else throw new AlreadyExistException(String.format(
                "отзыв с таким id %s уже зарегистрирован.", review.getReviewId()));
    }

    /**
     * редактирование отзыва
     */
    public Review updateReview(Review review) {
        final Review reviewFromStorage = reviewStorage.get(review.getReviewId());
        checkReview(reviewFromStorage);
        feedService.updateReviewFilmInFeed(reviewFromStorage.getUserId(), reviewFromStorage.getReviewId());
        reviewStorage.update(review);
        return review;
    }

    /**
     * удаление отзыва по id
     */
    public void deleteReviewById(long reviewId) {
        final Review reviewFromStorage = reviewStorage.get(reviewId);
        feedService.removeReviewFilmInFeed(reviewFromStorage.getUserId(), reviewId);
        reviewStorage.remove(reviewId);
    }

    /**
     * получение отзыва по id
     */
    public Review get(long reviewId) {
        final Review reviewFromStorage = reviewStorage.get(reviewId);
        checkReview(reviewFromStorage);
        return reviewFromStorage;
    }

    /**
     * Получение всех отзывов
     */
    public Collection<Review> getAllReview(int count) {
        return reviewStorage.getAllReview(count);
    }

    /**
     * ставим лайк отзыву
     */
    public void likeReview(long userId, long reviewId) {
        final Review reviewFromStorage = reviewStorage.get(reviewId);
        checkReview(reviewFromStorage);
        final User userInStorage = userStorage.get(userId);
        checkUser(userInStorage);
        ReviewLike reviewLike = new ReviewLike(userId, reviewId, true);
        reviewLikeStorage.create(reviewLike);
    }

    /**
     * ставим дизлайк отзыву
     */
    public void disLikeReview(long userId, long reviewId) {
        final Review reviewFromStorage = reviewStorage.get(reviewId);
        checkReview(reviewFromStorage);
        final User userInStorage = userStorage.get(userId);
        checkUser(userInStorage);
        ReviewLike reviewLike = new ReviewLike(userId, reviewId, false);
        reviewLikeStorage.create(reviewLike);
    }

    /**
     * удаление дизлайка/лайка отзыва
     */
    public void deleteLikeOrDisLike(long userId, long reviewId) {
        ReviewLike reviewLike = new ReviewLike(userId, reviewId, false);
        reviewLikeStorage.removeReviewLike(reviewLike);
    }

    /**
     * Получение всех отзывов по идентификатору фильма,
     * Если кол-во не указано то 10.
     */
    public Collection<Review> getAllReviewsByFilmId(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.getAllReview(count);

        } else {
            final Film filmFromStorage = filmStorage.get(filmId);
            checkFilm(filmFromStorage);
            return reviewStorage.getReviewByFilmId(filmId, count);
        }
    }

    private void checkReview(Review reviewFromStorage) {
        if (reviewFromStorage == null) {
            throw new NotFoundException("Review not found");
        }
    }

    private void checkUser(User userInStorage) {
        if (userInStorage == null) {
            throw new NotFoundException("User not found");
        }
    }

    private void checkFilm(Film filmFromStorage) {
        if (filmFromStorage == null) {
            throw new NotFoundException("Film not found");
        }
    }
}
