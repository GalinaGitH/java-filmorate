package ru.yandex.practicum.filmorate.DAO;

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
import java.util.Collection;
import java.util.List;


@Repository
@Primary
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Like> getLikes(long filmId, long userId) {
        String sqlQuery = "SELECT LIKES.USER_ID, LIKES.FILM_ID " +
                "FROM LIKES " +
                "WHERE LIKES.USER_ID = ? AND LIKES.FILM_ID = ? " +
                "LIMIT 1";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Like(rs.getLong("LIKES.USER_ID"),
                rs.getLong("LIKES.FILM_ID")), filmId, userId);
    }

    @Override
    public void addLikes(long filmId, long userId) {
        String sqlQuery = "MERGE INTO LIKES (USER_ID, FILM_ID) values (?,?) ";
        jdbcTemplate.update(sqlQuery
                , userId
                , filmId);
    }

    @Override
    public void removeLikes(long filmId, long userId) {
        String sqlQuery = "delete from LIKES where FILM_ID = ? OR USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Collection<Film> findPopularFilm(Integer size) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILM_NAME , FILM_RELEASE_DATE , FILM_DESCRIPTION ,FILM_DURATION , MPA.MPA_ID, MPA.MPA_TYPE " +
                "FROM FILMS " +
                "LEFT JOIN LIKES L on FILMS.FILM_ID = L.FILM_ID " +
                "JOIN MPA ON MPA.MPA_ID=FILMS.MPA_ID " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY COUNT (L.USER_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, size);
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

    @Override
    public Collection<Film> findPopularFilmsByYearAndGenres(Integer limit, int year, int genreId) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.FILM_NAME , FILMS.FILM_RELEASE_DATE , " +
                "FILMS.FILM_DESCRIPTION ,FILMS.FILM_DURATION, FILMS.MPA_ID, " +
                "MPA.MPA_ID, MPA.MPA_TYPE, " +
                "FILM_GENRES.FILM_ID, FILM_GENRES.GENRE_ID, " +
                "GENRE_NAMES.GENRE_NAME, " +
                "STATISTIC.CNT " +
                "FROM FILMS INNER JOIN (SELECT FILMS.FILM_ID, COUNT(LIKES.FILM_ID) AS CNT " +
                "FROM FILMS " +
                "LEFT JOIN LIKES on FILMS.FILM_ID = LIKES.FILM_ID " +
                "GROUP BY FILMS.FILM_ID ) AS STATISTIC ON FILMS.FILM_ID = STATISTIC.FILM_ID " +
                "LEFT JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID " +
                "JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID " +
                "JOIN GENRE_NAMES ON FILM_GENRES.GENRE_ID = GENRE_NAMES.GENRE_ID " +
                "WHERE CAST(EXTRACT(YEAR FROM FILMS.FILM_RELEASE_DATE) AS INTEGER) = ? AND " +
                "GENRE_NAMES.GENRE_ID = ? " +
                "ORDER BY STATISTIC.CNT DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, genreId, limit);
    }

    @Override
    public Collection<Film> findPopularFilmsByYear(Integer limit, int year) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.FILM_NAME , FILMS.FILM_RELEASE_DATE , " +
                "FILMS.FILM_DESCRIPTION ,FILMS.FILM_DURATION, FILMS.MPA_ID, " +
                "MPA.MPA_ID, MPA.MPA_TYPE, " +
                "STATISTIC.CNT " +
                "FROM FILMS INNER JOIN (SELECT FILMS.FILM_ID, COUNT(LIKES.FILM_ID) AS CNT " +
                "FROM FILMS " +
                "LEFT JOIN LIKES on FILMS.FILM_ID = LIKES.FILM_ID " +
                "GROUP BY FILMS.FILM_ID ) AS STATISTIC ON FILMS.FILM_ID = STATISTIC.FILM_ID " +
                "LEFT JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID " +
                "WHERE CAST(EXTRACT(YEAR FROM FILMS.FILM_RELEASE_DATE) AS INTEGER) = ? " +
                "ORDER BY STATISTIC.CNT DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, limit);
    }

    @Override
    public Collection<Film> findPopularFilmsByGenre(Integer limit, int genreId) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.FILM_NAME , FILMS.FILM_RELEASE_DATE , " +
                "FILMS.FILM_DESCRIPTION ,FILMS.FILM_DURATION, FILMS.MPA_ID, " +
                "MPA.MPA_ID, MPA.MPA_TYPE, " +
                "FILM_GENRES.FILM_ID, FILM_GENRES.GENRE_ID, " +
                "GENRE_NAMES.GENRE_NAME, " +
                "STATISTIC.CNT " +
                "FROM FILMS INNER JOIN (SELECT FILMS.FILM_ID, COUNT(LIKES.FILM_ID) AS CNT " +
                "FROM FILMS " +
                "LEFT JOIN LIKES on FILMS.FILM_ID = LIKES.FILM_ID " +
                "GROUP BY FILMS.FILM_ID ) AS STATISTIC ON FILMS.FILM_ID = STATISTIC.FILM_ID " +
                "LEFT JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID " +
                "JOIN FILM_GENRES ON FILMS.FILM_ID = FILM_GENRES.FILM_ID " +
                "JOIN GENRE_NAMES ON FILM_GENRES.GENRE_ID = GENRE_NAMES.GENRE_ID " +
                "WHERE GENRE_NAMES.GENRE_ID = ? " +
                "ORDER BY STATISTIC.CNT DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, limit);
    }
}
