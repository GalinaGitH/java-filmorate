package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Validated
@NoArgsConstructor
public class ReviewController {
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

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
    public void deleteReviewById(@PathVariable("id") long reviewId) {
        reviewService.deleteReviewById(reviewId);
        log.debug("Отзыв с id = {} удален из списка", reviewId);
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable("id") long reviewId) {
        log.debug("Получение отзыва с reviewId = {}", reviewId);
        return reviewService.get(reviewId);
    }

    @GetMapping("/reviews")
    public Collection<Review> getAllReviewByFilmId(@RequestParam(required = false) Long filmId, @RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        log.debug("Список отзывов фильма с filmid = {}", filmId);
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping(value = "/reviews/{id}/like/{userId}")
    public void likeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.likeReview(userId, reviewId);
        log.debug("Пользователь с id = {} поставил like отзыву с reviewId = {}", userId, reviewId);
    }

    @PutMapping(value = "/reviews/{id}/dislike/{userId}")
    public void disLikeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.disLikeReview(userId, reviewId);
        log.debug("Пользователь с id = {} поставил dislike отзыву с reviewId = {}", userId, reviewId);
    }

    @DeleteMapping(value = "/reviews/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.deleteLikeOrDisLike(userId, reviewId);
        log.debug("Пользователь с id = {} удалил свой like c отзыва reviewId = {}", userId, reviewId);
    }

    @DeleteMapping(value = "/reviews/{id}/dislike/{userId}")
    public void deleteDisLikeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.deleteLikeOrDisLike(userId, reviewId);
        log.debug("Пользователь с id = {} удалил свой dislike c отзыва reviewId = {}", userId, reviewId);
    }

}
