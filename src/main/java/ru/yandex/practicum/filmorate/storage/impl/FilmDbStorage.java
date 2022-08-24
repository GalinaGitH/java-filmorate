package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
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

        saveGenres(film);
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

        saveGenres(film);
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
        loadGenres(films);
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
        List<Film> filmsDB = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        loadGenres(filmsDB);
        return filmsDB;
    }

    @Override
    public void removeFilmById(long filmId) {
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Film> getLikedByUser(long userId) {
        String sqlQuery = "SELECT DISTINCT FILMS.FILM_ID, FILM_NAME , FILM_RELEASE_DATE , FILM_DESCRIPTION ,FILM_DURATION," +
                " MPA.MPA_ID, MPA_TYPE " +
                "FROM FILMS JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                "JOIN MPA  ON MPA.MPA_ID = FILMS.MPA_ID" +
                " WHERE LIKES.USER_ID = ?;";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId);
        loadGenres(films);
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
        loadGenres(films);
        return films;
    }

    @Override
    public List<Film> getFilmsFromIds(List <Long> idFilms) {
        String sql = String.join(",", Collections.nCopies(idFilms.size(), "?"));
        sql = String.format("select  FILM_ID, FILM_NAME , FILM_RELEASE_DATE , FILM_DESCRIPTION ,FILM_DURATION ," +
                " MPA.MPA_ID, MPA.MPA_TYPE " +
                "from FILMS Join MPA ON MPA.MPA_ID=FILMS.MPA_ID " +
                "where FILM_ID IN (%s)", sql);
        int[] argTypes = new int[idFilms.size()];
        Arrays.fill(argTypes, Types.BIGINT);
        List<Film> films = jdbcTemplate.query(sql, idFilms.toArray(), argTypes, this::mapRowToFilm);
        loadGenres(films);
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
        String qbuilder = " SELECT FILMS.FILM_ID, FILM_NAME, FILM_RELEASE_DATE, FILM_DESCRIPTION, FILM_DURATION, MPA.MPA_ID, MPA.MPA_TYPE, DIRECTOR_NAME" +
                " FROM" +
                " FILMS LEFT JOIN MPA ON MPA.MPA_ID=FILMS.MPA_ID LEFT JOIN FILM_DIRECTORS" +
                " ON FILMS.FILM_ID = FILM_DIRECTORS.FILM_ID" +
                " LEFT JOIN DIRECTORS ON DIRECTORS.DIRECTOR_ID = FILM_DIRECTORS.DIRECTOR_ID" +
                " WHERE " +
                String.join(" OR ", whereParts) +
                " ORDER BY FILMS.FILM_ID DESC" +
                ";";
        List<Film> films = jdbcTemplate.query(qbuilder, this::mapRowToFilm, params.toArray());
        loadGenres(films);
        return films;
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

    private void saveGenres(Film film) {
        long id = film.getId();
        String sqlDelete = "delete from FILM_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sqlDelete, id);
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            String sqlQuery = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) values (?,?) ";
            jdbcTemplate.update(sqlQuery
                    , id
                    , genre.getId());
        }
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
