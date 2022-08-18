package ru.yandex.practicum.filmorate.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
    public void addLikes(long filmId, long userId) {
        String sqlQuery = "INSERT INTO LIKES (USER_ID, FILM_ID) values (?,?) ";
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

}
