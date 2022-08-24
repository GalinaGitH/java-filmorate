package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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


    @Override
    public List<Film> findAll() {
        String sqlQuery = "select  FILM_ID, FILM_NAME , FILM_RELEASE_DATE , FILM_DESCRIPTION ,FILM_DURATION , MPA.MPA_ID, MPA.MPA_TYPE " +
                "from FILMS " +
                "Join MPA ON MPA.MPA_ID=FILMS.MPA_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public void removeFilmById(long filmId) {
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Film> getRecommended(long id) {
        String sqlQuery = " " + "SELECT DISTINCT F.FILM_ID, FILM_NAME, FILM_RELEASE_DATE, FILM_DESCRIPTION, " +
                "FILM_DURATION, M.MPA_ID, MPA_TYPE " +
                "FROM FILMS F JOIN MPA M ON M.MPA_ID = F.MPA_ID " +
                "JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                " WHERE L.FILM_ID NOT IN (SELECT FILM_ID FROM LIKES WHERE LIKES.USER_ID = ?) " +
                "AND L.FILM_ID IN (SELECT FILM_ID FROM LIKES WHERE USER_ID = " +
                "(SELECT DISTINCT USER_ID FROM LIKES WHERE FILM_ID IN " +
                "(SELECT FILM_ID FROM LIKES WHERE USER_ID = ?) " +
                "AND USER_ID <> ?" +
                ")" +
                ")";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id, id, id);
    }

    @Override
    public List<Film> getLikedByUser(long userId) {
        String sqlQuery = "SELECT DISTINCT FILMS.FILM_ID, FILM_NAME , FILM_RELEASE_DATE , FILM_DESCRIPTION ,FILM_DURATION," +
                " MPA.MPA_ID, MPA_TYPE " +
                "FROM FILMS JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                "JOIN MPA  ON MPA.MPA_ID = FILMS.MPA_ID" +
                " WHERE LIKES.USER_ID = ?;";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId);
        return films;
    }

    @Override
    public List<Film> getLikedByUserSortedPopular(long userId) {
        String sqlQuery = "SELECT DISTINCT FILMS.FILM_ID, FILM_NAME , FILM_RELEASE_DATE , FILM_DESCRIPTION ,FILM_DURATION, COUNT(L.USER_ID) AS CNT, " +
                " MPA.MPA_ID, MPA_TYPE, L.USER_ID " +
                " FROM FILMS JOIN LIKES AS L ON FILMS.FILM_ID = L.FILM_ID " +
                " JOIN MPA  ON MPA.MPA_ID = FILMS.MPA_ID " +
                " WHERE L.USER_ID = ? " +
                " GROUP BY FILMS.FILM_ID " +
                " ORDER BY CNT DESC;";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId);
        return films;
    }

    @Override
    public List<Film> search(String query, List<String> by) {
        List<String> params = new ArrayList<>();
        List<String> whereParts = Map.of(
                        "director", "LOWER(DIRECTOR_NAME) LIKE ?",
                        "title", "LOWER(FILM_NAME) LIKE ?")
                .entrySet()
                .stream()
                .filter(entry -> by.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .peek(entry -> params.add("%" + query.toLowerCase() + "%"))
                .collect(Collectors.toList());
        StringBuilder qbuilder = new StringBuilder();
        qbuilder
                .append(" SELECT FILMS.FILM_ID, FILM_NAME, FILM_RELEASE_DATE, FILM_DESCRIPTION, FILM_DURATION, MPA.MPA_ID, MPA.MPA_TYPE, DIRECTOR_NAME")
                .append(" FROM")
                .append(" FILMS LEFT JOIN MPA ON MPA.MPA_ID=FILMS.MPA_ID LEFT JOIN FILM_DIRECTORS")
                .append(" ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID")
                .append(" LEFT JOIN DIRECTORS ON DIRECTORS.DIRECTOR_ID = FILM_DIRECTORS.DIRECTOR_ID")
                .append(" WHERE ")
                .append(String.join(" OR ", whereParts))
                .append(" ORDER BY FILMS.FILM_ID DESC")
                .append(";");
        return jdbcTemplate.query(qbuilder.toString(), this::mapRowToFilm, params.toArray());
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
