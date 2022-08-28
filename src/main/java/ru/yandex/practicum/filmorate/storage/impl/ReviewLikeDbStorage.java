package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

@Repository
@Primary
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewLikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(ReviewLike reviewLike) {
        String sqlQuery = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_USEFUL) VALUES (?,?,?)";
        jdbcTemplate.update(sqlQuery
                , reviewLike.getReviewId()
                , reviewLike.getUserId()
                , reviewLike.isUseful());

        if (reviewLike.isUseful()) {
            incrementUseful(reviewLike.getReviewId());
        } else {
            decrementUseful(reviewLike.getReviewId());
        }
    }

    @Override
    public void removeReviewLike(ReviewLike reviewLike) {
        String sqlQuery = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewLike.getReviewId(), reviewLike.getUserId());

        if (reviewLike.isUseful()) {
            decrementUseful(reviewLike.getReviewId());
        } else {
            incrementUseful(reviewLike.getReviewId());
        }
    }

    private void incrementUseful(long reviewId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    private void decrementUseful(long reviewId) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

}
