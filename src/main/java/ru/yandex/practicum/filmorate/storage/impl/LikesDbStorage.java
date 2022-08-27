package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
@Primary
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Like> getLike(long filmId, long userId) {
        String sqlQuery = "SELECT LIKES.USER_ID, LIKES.FILM_ID, LIKES.SCORE " +
                "FROM LIKES " +
                "WHERE LIKES.USER_ID = ? AND LIKES.FILM_ID = ? " +
                "LIMIT 1";
        List<Like> likes = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Like(rs.getLong("LIKES.USER_ID"),
                rs.getLong("LIKES.FILM_ID"), rs.getInt("LIKES.SCORE")), userId, filmId);
        if (likes.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(likes.get(0));
        }
    }

    @Override
    public List<Like> getTopLikes(int limit) {
        String sqlQuery = "SELECT LIKES.USER_ID, LIKES.FILM_ID, LIKES.SCORE " +
                "FROM LIKES " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Like(rs.getLong("LIKES.USER_ID"),
                rs.getLong("LIKES.FILM_ID"), rs.getInt("LIKES.SCORE")), limit);
    }

    @Override
    public void addLike(long filmId, long userId, Integer score) {
        String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID, SCORE) values (?,?,?) ";
        jdbcTemplate.update(sqlQuery
                , filmId
                , userId
                , score
        );

        updateRating(filmId);
    }

    @Override
    public void updateLike(long filmId, long userId, Integer score) {
        String sqlQuery = "MERGE INTO LIKES (FILM_ID, USER_ID, SCORE) values (?,?,?) ";
        jdbcTemplate.update(sqlQuery
                , filmId
                , userId
                , score
        );

        updateRating(filmId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);

        updateRating(filmId);
    }

    @Override
    public List<Film> findPopularFilm(Integer size) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILM_NAME, FILM_RELEASE_DATE, FILM_DESCRIPTION, FILM_DURATION," +
                " MPA.MPA_ID, MPA.MPA_TYPE " +
                "FROM FILMS " +
                "JOIN MPA ON MPA.MPA_ID=FILMS.MPA_ID " +
                "GROUP BY FILMS.FILM_ID, FILMS.RATING " +
                "ORDER BY FILMS.RATING DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, size);
    }


    @Override
    public List<Film> findPopularFilmsByYearAndGenres(Integer limit, int year, int genreId) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.FILM_NAME , FILMS.FILM_RELEASE_DATE , " +
                "       FILMS.FILM_DESCRIPTION ,FILMS.FILM_DURATION, FILMS.MPA_ID, MPA_TYPE " +
                "FROM FILMS " +
                "         LEFT JOIN FILM_GENRES AS FG ON FILMS.FILM_ID = FG.FILM_ID " +
                "         JOIN MPA AS M ON FILMS.MPA_ID = M.MPA_ID " +
                "WHERE CAST(EXTRACT(YEAR FROM FILMS.FILM_RELEASE_DATE) AS INTEGER) = ? AND FG.GENRE_ID = ? " +
                "GROUP BY FILMS.FILM_ID, FILMS.RATING " +
                "ORDER BY FILMS.RATING DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, genreId, limit);
    }

    @Override
    public List<Film> findPopularFilmsByYear(Integer limit, int year) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.FILM_NAME , FILMS.FILM_RELEASE_DATE , " +
                "       FILMS.FILM_DESCRIPTION ,FILMS.FILM_DURATION, FILMS.MPA_ID, MPA_TYPE " +
                "FROM FILMS " +
                "         LEFT JOIN FILM_GENRES AS FG ON FILMS.FILM_ID = FG.FILM_ID " +
                "JOIN MPA M on M.MPA_ID = FILMS.MPA_ID " +
                "WHERE CAST(EXTRACT(YEAR FROM FILMS.FILM_RELEASE_DATE) AS INTEGER) = ? " +
                "GROUP BY FILMS.FILM_ID, FILMS.RATING " +
                "ORDER BY FILMS.RATING DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, limit);
    }

    @Override
    public List<Film> findPopularFilmsByGenre(Integer limit, int genreId) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.FILM_NAME , FILMS.FILM_RELEASE_DATE , " +
                "       FILMS.FILM_DESCRIPTION ,FILMS.FILM_DURATION, FILMS.MPA_ID, MPA_TYPE " +
                "FROM FILMS " +
                "         LEFT JOIN FILM_GENRES AS FG ON FILMS.FILM_ID = FG.FILM_ID " +
                "JOIN MPA M on M.MPA_ID = FILMS.MPA_ID " +
                "WHERE FG.GENRE_ID = ? " +
                "GROUP BY FILMS.FILM_ID, FILMS.RATING " +
                "ORDER BY FILMS.RATING DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, limit);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .releaseDate(resultSet.getDate("FILM_RELEASE_DATE").toLocalDate())
                .description(resultSet.getString("FILM_DESCRIPTION"))
                .duration(resultSet.getLong("FILM_DURATION"))
                .mpa(new Mpa(resultSet.getInt("MPA.MPA_ID"), resultSet.getString("MPA.MPA_TYPE")))
                .build();
        return film;
    }

    private void updateRating(long filmId) {
        String sqlQuery = "UPDATE FILMS SET (RATING) =" +
                " (SELECT ROUND(AVG(SCORE),1) FROM LIKES WHERE LIKES.FILM_ID = ?)" +
                " WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, filmId);
    }

}
