package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director getById(int id) {
        final String sqlQuery = "select DIRECTOR_ID ,DIRECTOR_NAME " +
                "FROM DIRECTORS " +
                "where DIRECTOR_ID = ?";
        final List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id);
        if (directors.size() != 1) {
            return null;
        }
        return directors.get(0);
    }

    @Override
    public List<Director> getAll() {
        String sqlQuery = "select  DIRECTOR_ID ,DIRECTOR_NAME " +
                "FROM DIRECTORS ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    /**
     * запись режиссеров в БД ,заполняем таблицу FILM_DIRECTORS
     */
    @Override
    public void setFilmDirector(Film film) {
        long id = film.getId();
        String sqlDelete = "delete from FILM_DIRECTORS where FILM_ID = ?";

        jdbcTemplate.update(sqlDelete, id);
        if (film.getDirectors() == null) {
            film.setDirectors(new HashSet<Director>());
            return;
        }
        if (film.getDirectors().isEmpty()) {
            return;
        }
        for (Director director : film.getDirectors()) {
            String sqlQuery = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) values (?,?) ";
            jdbcTemplate.update(sqlQuery
                    , id
                    , director.getId());
        }
    }

    /**
     * получаем режиссерав 1-го фильма
     */
    @Override
    public List<Director> loadFilmDirector(Film film) {
        long filmId = film.getId();
        String sqlQuery = "SELECT DIRECTOR_ID, DIRECTOR_NAME " +
                "FROM DIRECTORS " +
                "WHERE DIRECTOR_ID IN (SELECT DIRECTOR_ID " +
                "FROM FILM_DIRECTORS " +
                "WHERE FILM_ID = ?)";

        final List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId);
        return directors;

    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "insert into DIRECTORS (DIRECTOR_NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "update DIRECTORS set " +
                "DIRECTOR_NAME = ? " +
                "where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery
                , director.getName()
                , director.getId());
        return director;
    }

    @Override
    public void remove(Director director) {
        int id = director.getId();
        String sqlQuery = "delete from DIRECTORS where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> getSortedFilmsByYearOfDirector(int idDirector) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILM_NAME, FILM_RELEASE_DATE, FILM_DESCRIPTION, FILM_DURATION," +
                " MPA.MPA_ID, MPA_TYPE  " +
                "FROM FILMS JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                "WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?) " +
                "ORDER BY FILM_RELEASE_DATE;";
        final List<Film> sortedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, idDirector);
        loadGenres(sortedFilms);
        return sortedFilms;
    }

    @Override
    public List<Film> getSortedFilmsByLikesOfDirector(int idDirector) {
        String sqlQuery = "SELECT AVG (LIKES.SCORE) as RATING, FILMS.FILM_ID, FILM_NAME, FILM_RELEASE_DATE, FILM_DESCRIPTION," +
                " FILM_DURATION, MPA.MPA_ID, MPA_TYPE " +
                "FROM LIKES JOIN FILMS ON LIKES.FILM_ID = FILMS.FILM_ID JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID" +
                " WHERE FILMS.FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?)" +
                " GROUP BY FILMS.FILM_ID" +
                " ORDER BY RATING;";
        List<Film> sortedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, idDirector);


        if (sortedFilms.isEmpty()) { //если лайков нет на конкретного режиссера
            sqlQuery = "SELECT FILMS.FILM_ID, FILM_NAME, FILM_RELEASE_DATE, FILM_DESCRIPTION, FILM_DURATION," +
                    " MPA.MPA_ID, MPA_TYPE  " +
                    "FROM FILMS JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                    "WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?) " +
                    ";";
            sortedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, idDirector);
        }
        loadGenres(sortedFilms);
        return sortedFilms;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("DIRECTOR_ID"))
                .name(resultSet.getString("DIRECTOR_NAME"))
                .build();
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
    private void loadGenres(List<Film> films) {
        Map<Long, Film> idToFilm = films
                .stream()
                .collect(Collectors
                        .toMap(Film::getId, film -> {
                            film.setGenres(new HashSet<Genre>());
                            return film;
                        }, (a, b) -> b));
        List<Long> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        String sql = String.join(",", Collections.nCopies(ids.size(), "?"));
        sql = String.format("SELECT FG.GENRE_ID, FG.FILM_ID, GN.GENRE_NAME " +
                "FROM FILM_GENRES AS FG JOIN GENRE_NAMES AS GN ON GN.GENRE_ID = FG.GENRE_ID" +
                " WHERE FG.FILM_ID IN (%s)", sql);

        int[] argTypes = new int[ids.size()];
        Arrays.fill(argTypes, Types.BIGINT);

        jdbcTemplate.query(sql, ids.toArray(), argTypes,
                (rs, rowNum) ->
                        idToFilm.get(rs.getLong("FILM_GENRES.FILM_ID")).getGenres()
                                .add(new Genre(rs.getInt("FILM_GENRES.GENRE_ID"),
                                        rs.getString("GENRE_NAMES.GENRE_NAME")))
        );
    }
}
