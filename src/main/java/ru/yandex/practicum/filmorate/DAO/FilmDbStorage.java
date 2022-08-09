package ru.yandex.practicum.filmorate.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "insert into FILMS (FILM_NAME,FILM_RELEASE_DATE,FILM_DESCRIPTION,FILM_DURATION,MPA_ID) values (?, ?, ?, ?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            final LocalDate releaseDate = film.getReleaseDate();
            stmt.setDate(2, Date.valueOf(releaseDate));
            stmt.setString(3, film.getDescription());
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        genreDbStorage.setFilmGenre(film);//записываем жанры фильму,заполняем таблицу FILM_GENRES
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update FILMS set " +
                "FILM_NAME = ?, FILM_RELEASE_DATE = ?, FILM_DESCRIPTION = ?,FILM_DURATION = ? , MPA_ID =? " +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getReleaseDate()
                , film.getDescription()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        genreDbStorage.setFilmGenre(film); // заново заполняем данные по жанрам для фильма
        return film;
    }

    @Override
    public void remove(Film film) {
        long id = film.getId();
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film get(long filmId) {
        final String sqlQuery = "select FILM_ID, FILM_NAME , FILM_RELEASE_DATE , FILM_DESCRIPTION ,FILM_DURATION , MPA.MPA_ID, MPA.MPA_TYPE " +
                "from FILMS " +
                "Join MPA ON MPA.MPA_ID=FILMS.MPA_ID " +
                "where FILM_ID = ?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, filmId);
        if (films.size() != 1) {
            return null;
        }
        return films.get(0);
    }

    Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .releaseDate(resultSet.getDate("FILM_RELEASE_DATE").toLocalDate())
                .description(resultSet.getString("FILM_DESCRIPTION"))
                .duration(resultSet.getLong("FILM_DURATION"))
                .mpa(new Mpa(resultSet.getInt("MPA.MPA_ID"), resultSet.getString("MPA.MPA_TYPE")))
                .build();
        film.setGenres(new HashSet<>(genreDbStorage.loadFilmGenre(film))); //получаем жанры фильма
        return film;
    }
    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "select  FILM_ID, FILM_NAME , FILM_RELEASE_DATE , FILM_DESCRIPTION ,FILM_DURATION , MPA.MPA_ID, MPA.MPA_TYPE " +
                "from FILMS " +
                "Join MPA ON MPA.MPA_ID=FILMS.MPA_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }
}
