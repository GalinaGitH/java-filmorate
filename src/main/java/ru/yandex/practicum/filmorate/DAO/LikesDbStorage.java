package ru.yandex.practicum.filmorate.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;


@Repository
@Primary
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
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
                "GROUP BY FILM_NAME " +
                "ORDER BY COUNT (L.USER_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, filmDbStorage::mapRowToFilm, size);
    }
}
