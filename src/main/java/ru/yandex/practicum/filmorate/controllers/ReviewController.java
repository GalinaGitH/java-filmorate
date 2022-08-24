package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/reviews")
    public Review create(@Valid @RequestBody Review review) {
        log.debug("Количество добавленных отзывов: {}", 1);
        return reviewService.saveReview(review);
    }

    @PutMapping(value = "/reviews")
    public Review update(@Valid @RequestBody Review review) {
        reviewService.updateReview(review);
        log.debug("Отзыв обновлен");
        return review;
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteById(@PathVariable("id") long reviewId) {
        reviewService.deleteReviewById(reviewId);
        log.debug("Отзыв с id = {} удален из списка", reviewId);
    }

    @GetMapping("/reviews/{id}")
    public Review getById(@PathVariable("id") long reviewId) {
        log.debug("Получение отзыва с reviewId = {}", reviewId);
        return reviewService.get(reviewId);
    }

    @GetMapping("/reviews")
    public List<Review> getAllByFilmId(@RequestParam(required = false) Long filmId,
                                       @RequestParam(value = "count", defaultValue = "10", required = false)
                                       @Positive int count) {
        log.debug("Список отзывов фильма с filmid = {}", filmId);
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping(value = "/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.likeReview(userId, reviewId);
        log.debug("Пользователь с id = {} поставил like отзыву с reviewId = {}", userId, reviewId);
    }

    @PutMapping(value = "/reviews/{id}/dislike/{userId}")
    public void addDisLike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.disLikeReview(userId, reviewId);
        log.debug("Пользователь с id = {} поставил dislike отзыву с reviewId = {}", userId, reviewId);
    }

    @DeleteMapping(value = "/reviews/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.deleteLikeOrDisLike(userId, reviewId);
        log.debug("Пользователь с id = {} удалил свой like c отзыва reviewId = {}", userId, reviewId);
    }

    @DeleteMapping(value = "/reviews/{id}/dislike/{userId}")
    public void deleteDisLike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.deleteLikeOrDisLike(userId, reviewId);
        log.debug("Пользователь с id = {} удалил свой dislike c отзыва reviewId = {}", userId, reviewId);
    }

}
