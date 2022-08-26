package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public Optional<Mpa> getById(long id) {
        final String sqlQuery = "select MPA_ID ,MPA_TYPE " +
                "FROM MPA " +
                "where MPA_ID = ?";
        final List<Mpa> mpas = jdbcTemplate.query(sqlQuery, this::makeMpa, id);
        if (mpas.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(mpas.get(0));
    }

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "select  MPA_ID ,MPA_TYPE " +
                "FROM MPA ";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("MPA_ID"),
                rs.getString("MPA_TYPE")
        );
    }
}
