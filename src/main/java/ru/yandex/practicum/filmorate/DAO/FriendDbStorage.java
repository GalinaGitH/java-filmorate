package ru.yandex.practicum.filmorate.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@Primary
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) values (?,?) ";
        jdbcTemplate.update(sqlQuery
                , userId
                , friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlQuery = "delete from FRIENDS where USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    //этот метод пока не нужен
    /*public void removeAllFriends(long userId){
        String sqlQuery = "delete from FRIENDS where USER_ID = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }*/

    @Override
    public Collection<User> findAllCommonFriends(long userId, long friendId) {
        Collection<User> friendsSet1 = getListOfFriends(userId);
        Collection<User> friendsSet2 = getListOfFriends(friendId);
        friendsSet1.retainAll(friendsSet2);
        return friendsSet1;
    }

    @Override
    public Collection<User> getListOfFriends(long userId) {
        String sqlQuery = "select U2.USER_ID, U2.USER_NAME, U2.USER_EMAIL, U2.USER_LOGIN, U2.USER_BIRTHDAY " +
                "from USERS U " +
                "Join FRIENDS F ON U.USER_ID=F.USER_ID " +
                "Join USERS U2 ON U2.USER_ID= F.FRIEND_ID " +
                "where U.USER_ID = ?";
        final List<User> friends = jdbcTemplate.query(sqlQuery, this::mapRowToFriend, userId);
        return friends;
    }

    private User mapRowToFriend(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .name(resultSet.getString("USER_NAME"))
                .email(resultSet.getString("USER_EMAIL"))
                .login(resultSet.getString("USER_LOGIN"))
                .birthday(resultSet.getDate("USER_BIRTHDAY").toLocalDate())
                .build();
    }
}
