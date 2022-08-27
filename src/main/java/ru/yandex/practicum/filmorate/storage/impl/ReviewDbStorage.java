package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Review> get(long reviewId) {
        final String sqlQuery = " SELECT REVIEW_ID, USER_ID, FILM_ID, IS_POSITIVE, CONTENT, USEFUL " +
                " FROM REVIEWS " +
                " WHERE REVIEW_ID = ?";
        final List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview, reviewId);
        if (reviews.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(reviews.get(0));
    }

    @Override
    public Review create(Review review) {
        String sqlQuery = " INSERT INTO REVIEWS (USER_ID,FILM_ID,IS_POSITIVE,CONTENT) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setLong(1, review.getUserId());
            stmt.setLong(2, review.getFilmId());
            stmt.setBoolean(3, review.getIsPositive());
            stmt.setString(4, review.getContent());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = " UPDATE REVIEWS SET " +
                " IS_POSITIVE = ?, CONTENT = ? " +
                " WHERE REVIEW_ID= ?";
        jdbcTemplate.update(sqlQuery
                , review.getIsPositive()
                , review.getContent()
                , review.getReviewId());
        return review;
    }

    @Override
    public void remove(long reviewId) {
        String sqlQuery = " DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public List<Review> getReviewByFilmId(long filmId, int count) {
        final String sqlQuery = " SELECT REVIEW_ID, USER_ID, FILM_ID, IS_POSITIVE, CONTENT, USEFUL " +
                " FROM REVIEWS " +
                " WHERE FILM_ID = ?" +
                " GROUP BY REVIEW_ID " +
                " ORDER BY USEFUL DESC " +
                "LIMIT ?";
        final List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
        return reviews;
    }

    @Override
    public List<Review> getAllReview(int count) {
        final String sqlQuery = " SELECT REVIEW_ID, USER_ID, FILM_ID, IS_POSITIVE, CONTENT, USEFUL " +
                " FROM REVIEWS " +
                " GROUP BY REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        final List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
        return reviews;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review rev = Review.builder()
                .reviewId(resultSet.getLong("REVIEW_ID"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .content(resultSet.getString("CONTENT"))
                .useful(resultSet.getInt("USEFUL"))
                .build();

        return rev;
    }

}
