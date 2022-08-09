package ru.yandex.practicum.filmorate.DAO;

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
import java.util.Collection;
import java.util.List;

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
        String sqlQuery = "insert into USERS (USER_NAME,USER_EMAIL,USER_LOGIN,USER_BIRTHDAY) values (?, ?, ?, ?)";
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
        long id = user.getId();
        return get(id);
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update USERS set " +
                "USER_NAME = ?, USER_EMAIL = ?, USER_LOGIN = ?,USER_BIRTHDAY = ? " +
                "where USER_ID= ?";
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
        String sqlQuery = "delete from USERS where USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User get(long userId) {
        final String sqlQuery = "select USER_ID,USER_NAME,USER_EMAIL,USER_LOGIN,USER_BIRTHDAY " +
                "from USERS " +
                "where USER_ID = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
        if (users.size() != 1) {
            return null;
        }
        return users.get(0);
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

    @Override
    public Collection<User> findAllUsers() {
        String sqlQuery = "select USER_ID,USER_NAME,USER_EMAIL,USER_LOGIN,USER_BIRTHDAY " +
                "from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }
}
