package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        final String sqlQuery = "select REVIEW_ID, USER_ID, FILM_ID, IS_POSITIVE, CONTENT " +
                "from REVIEWS " +
                "where REVIEW_ID = ?";
        final List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview, reviewId);
        if (reviews.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(reviews.get(0));
    }

    @Override
    public Review create(Review review) {
        String sqlQuery = "insert into REVIEWS (USER_ID,FILM_ID,IS_POSITIVE,CONTENT) values (?, ?, ?, ?)";
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
        String sqlQuery = "update REVIEWS set " +
                "IS_POSITIVE = ?,CONTENT = ? " +
                "where REVIEW_ID= ?";
        jdbcTemplate.update(sqlQuery
                , review.getIsPositive()
                , review.getContent()
                , review.getReviewId());
        return review;
    }

    @Override
    public void remove(long reviewId) {
        String sqlQuery = "delete from REVIEWS where REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public List<Review> getReviewByFilmId(long filmId, int count) {
        final String sqlQuery = "select REVIEW_ID, USER_ID, FILM_ID, IS_POSITIVE, CONTENT " +
                "from REVIEWS " +
                "where FILM_ID = ?" +
                "GROUP BY REVIEW_ID ";
        final List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId);
        List<Review> sorted = reviews.stream()
                .sorted(Comparator.comparing(e -> e.getUseful(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
        return sorted;
    }

    @Override
    public List<Review> getAllReview(int count) {
        final String sqlQuery = "select REVIEW_ID, USER_ID, FILM_ID, IS_POSITIVE, CONTENT " +
                "from REVIEWS " +
                "GROUP BY REVIEW_ID ";
        final List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview);
        List<Review> sorted = reviews
                .stream()
                .sorted(Comparator.comparing(e -> e.getUseful(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
        return sorted;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review rev = Review.builder()
                .reviewId(resultSet.getLong("REVIEW_ID"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .content(resultSet.getString("CONTENT"))
                .build();
        rev.setUseful(getUsefulRate(rev.getReviewId())); //записываем рейтинг полезности
        return rev;
    }

    /**
     * получение/расчет рейтинга полезности
     * отзывы должны сортироваться по рейтингу полезности
     * При создании отзыва рейтинг равен нулю.
     * Если пользователь оценил отзыв как полезный, это увеличивает его рейтинг на 1.
     * Если как бесполезный, то уменьшает на 1.
     */
    private int getUsefulRate(long reviewId) {
        int usefulRate = 0;
        try {
            Object[] cashflowQuefyArgs = new Object[]{reviewId, reviewId};
            final String sqlQuery = "SELECT COUNT(USER_ID) AS usefulRate " +
                    "FROM REVIEW_LIKES " +
                    "WHERE IS_USEFUL = TRUE  AND REVIEW_ID = ? " +
                    "GROUP BY REVIEW_ID " +
                    "UNION ALL " +
                    "SELECT - 1 * COUNT(USER_ID) AS usefulRate " +
                    "FROM REVIEW_LIKES " +
                    "WHERE IS_USEFUL = FALSE AND REVIEW_ID = ?" +
                    "GROUP BY REVIEW_ID";
            usefulRate = jdbcTemplate.queryForObject(sqlQuery, cashflowQuefyArgs,
                    new int[]{Types.BIGINT, Types.BIGINT}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return usefulRate;
        }
        return usefulRate;
    }
}
