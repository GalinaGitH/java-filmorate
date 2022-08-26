package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


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
        userStorage
                .get(review.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        filmStorage
                .get(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Film not found"));

        reviewStorage
                .get(review.getReviewId())
                .ifPresent(
                        (val) -> {
                            throw new AlreadyExistException(String.format(
                                    "отзыв с таким id %s уже зарегистрирован.", review.getReviewId()));
                        }
                );

        Review newReview = reviewStorage.create(review);
        feedService.addReviewFilmInFeed(newReview.getUserId(), newReview.getReviewId());

        return newReview;
    }

    /**
     * редактирование отзыва
     */
    public Review updateReview(Review review) {

        final Review reviewFromStorage = reviewStorage
                .get(review.getReviewId())
                .orElseThrow(() -> new NotFoundException("Review not found"));

        feedService.updateReviewFilmInFeed(reviewFromStorage.getUserId(), reviewFromStorage.getReviewId());
        reviewStorage.update(review);
        return review;
    }

    /**
     * удаление отзыва по id
     */
    public void deleteReviewById(long reviewId) {

        reviewStorage
                .get(reviewId)
                .ifPresent(
                        (val) -> {
                            feedService.removeReviewFilmInFeed(val.getUserId(), reviewId);
                            reviewStorage.remove(reviewId);
                        }
                );
    }

    /**
     * получение отзыва по id
     */
    public Review get(long reviewId) {

        return reviewStorage
                .get(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
    }

    /**
     * ставим лайк отзыву
     */
    public void likeReview(long userId, long reviewId) {

        reviewStorage
                .get(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ReviewLike reviewLike = new ReviewLike(userId, reviewId, true);
        reviewLikeStorage.create(reviewLike);
    }

    /**
     * ставим дизлайк отзыву
     */
    public void disLikeReview(long userId, long reviewId) {

        reviewStorage
                .get(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

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
    public List<Review> getAllReviewsByFilmId(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.getAllReview(count);

        } else {
            filmStorage.get(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
            return reviewStorage.getReviewByFilmId(filmId, count);
        }
    }

}
