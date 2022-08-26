package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> getById(long id) {
        final String sqlQuery = "select GENRE_ID ,GENRE_NAME " +
                "FROM GENRE_NAMES " +
                "where GENRE_ID = ?";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        if (genres.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(genres.get(0));
    }


    @Override
    public List<Genre> getAll() {
        String sqlQuery = "select  GENRE_ID ,GENRE_NAME " +
                "FROM GENRE_NAMES ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }


    /**
     * получаем жанры  1-го фильма
     */
    @Override
    public List<Genre> loadFilmGenre(Film film) {
        long filmId = film.getId();
        String sqlQuery = "SELECT GENRE_ID, GENRE_NAME " +
                "FROM GENRE_NAMES " +
                "WHERE GENRE_ID IN (SELECT GENRE_ID " +
                "FROM FILM_GENRES " +
                "WHERE FILM_ID = ?)";

        final List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
        return genres;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }

}