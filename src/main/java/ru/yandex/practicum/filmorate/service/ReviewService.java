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

    /**
     * сохранение отзыва
     */
    public Review saveReview(Review review) {
        final User userInStorage = userStorage.get(review.getUserId());
        if (userInStorage == null) {
            throw new NotFoundException("User with id=" + review.getUserId() + "not found");
        }
        final Film film = filmStorage.get(review.getFilmId());
        if (film == null) {
            throw new NotFoundException("Film with id=" + review.getFilmId() + "not found");
        }
        final Review reviewFromStorage = reviewStorage.get(review.getReviewId());
        if (reviewFromStorage == null) {
            return reviewStorage.create(review);
        } else throw new AlreadyExistException(String.format(
                "отзыв с таким id %s уже зарегистрирован.", review.getReviewId()));
    }

    /**
     * редактирование отзыва
     */
    public Review updateReview(Review review) {
        final Review reviewFromStorage = reviewStorage.get(review.getReviewId());
        if (reviewFromStorage == null) {
            throw new NotFoundException("Review with id=" + review.getReviewId() + "not found");
        }
        reviewStorage.update(review);
        return review;
    }

    /**
     * удаление отзыва по id
     */
    public void deleteReviewById(long reviewId) {
        final Review reviewFromStorage = reviewStorage.get(reviewId);
        if (reviewFromStorage == null) {
            throw new NotFoundException("Review with id=" + reviewId + "not found");
        }
        reviewStorage.remove(reviewId);
    }

    /**
     * получение отзыва по id
     */
    public Review get(long reviewId) {
        final Review reviewFromStorage = reviewStorage.get(reviewId);
        if (reviewFromStorage == null) {
            throw new NotFoundException("Review with id=" + reviewId + "not found");
        }
        return reviewFromStorage;
    }


    /**
     * Получение всех отзывов по идентификатору фильма,
     * Если кол-во не указано то 10.
     */
    public Collection<Review> getAllReviewByFilmId(long filmId, int count) {
        final Film filmFromStorage = filmStorage.get(filmId);
        if (filmFromStorage == null) {
            throw new NotFoundException("Film with id=" + filmId + "not found");
        }
        return reviewStorage.getReviewByFilmId(filmId, count);
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
        if (reviewFromStorage == null) {
            throw new NotFoundException("Review with id=" + reviewId + "not found");
        }
        final User userInStorage = userStorage.get(userId);
        if (userInStorage == null) {
            throw new NotFoundException("User with id=" + userId + "not found");
        }
        ReviewLike reviewLike = new ReviewLike(userId, reviewId, true);
        reviewLikeStorage.create(reviewLike);
    }

    /**
     * ставим дизлайк отзыву
     */
    public void disLikeReview(long userId, long reviewId) {
        final Review reviewFromStorage = reviewStorage.get(reviewId);
        if (reviewFromStorage == null) {
            throw new NotFoundException("Review with id=" + reviewId + "not found");
        }
        final User userInStorage = userStorage.get(userId);
        if (userInStorage == null) {
            throw new NotFoundException("User with id=" + userId + "not found");
        }
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

}
