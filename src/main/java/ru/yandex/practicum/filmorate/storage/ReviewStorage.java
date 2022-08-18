package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Review get(long reviewId);

    Review create(Review review);

    Review update(Review review);

    void remove(long reviewId);

    Collection<Review> getReviewByFilmId(long filmId, int count);

    Collection<Review> getAllReview(int count);
}
