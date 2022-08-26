package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> get(long reviewId);

    Review create(Review review);

    Review update(Review review);

    void remove(long reviewId);

    List<Review> getReviewByFilmId(long filmId, int count);

    List<Review> getAllReview(int count);
}
