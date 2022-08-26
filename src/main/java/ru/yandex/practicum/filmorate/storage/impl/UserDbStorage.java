package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO USERS (USER_NAME,USER_EMAIL,USER_LOGIN,USER_BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            final LocalDate birthday = user.getBirthday();
            if (birthday == null) {
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(4, Date.valueOf(birthday));
            }
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE USERS SET " +
                "USER_NAME = ?, USER_EMAIL = ?, USER_LOGIN = ?,USER_BIRTHDAY = ? " +
                "WHERE USER_ID= ?";
        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getEmail()
                , user.getLogin()
                , user.getBirthday()
                , user.getId());
        return user;
    }

    @Override
    public void remove(User user) {
        long id = user.getId();
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Optional<User> get(long userId) {
        final String sqlQuery = "SELECT USER_ID,USER_NAME,USER_EMAIL,USER_LOGIN,USER_BIRTHDAY " +
                "FROM USERS " +
                "WHERE USER_ID = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
        if (users.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "SELECT USER_ID,USER_NAME,USER_EMAIL,USER_LOGIN,USER_BIRTHDAY " +
                "FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public void removeUserById(long userId) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .name(resultSet.getString("USER_NAME"))
                .email(resultSet.getString("USER_EMAIL"))
                .login(resultSet.getString("USER_LOGIN"))
                .birthday(resultSet.getDate("USER_BIRTHDAY").toLocalDate())
                .build();
    }

}
