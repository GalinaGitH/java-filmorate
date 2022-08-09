package ru.yandex.practicum.filmorate.DAO;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendStorageTest {
    /*private final UserDbStorage userStorage;
    private final FriendDbStorage friendStorage;


    @Test
    public void addFriendTestAndGetListOfFriends() {
        User newUser = new User(0, "alex@mail.ru", "newuser", "Alex", LocalDate.of(1985, 4, 19));
        userStorage.createUser(newUser);
        User nextUser = new User(0, "piterPen@mail.ru", "nextuser", "Piter", LocalDate.of(1987, 3, 12));
        userStorage.createUser(nextUser);
        long userId = newUser.getId();
        long friendId = nextUser.getId();
        friendStorage.addFriend(userId, friendId);
        assertEquals(1, friendStorage.getListOfFriends(userId).size());
    }

    @Test
    public void removeFriendTest() {
        User newUser = new User(0, "alex@mail.ru", "newuser", "Alex", LocalDate.of(1985, 4, 19));
        userStorage.createUser(newUser);
        User nextUser = new User(0, "piterPen@mail.ru", "nextuser", "Piter", LocalDate.of(1987, 3, 12));
        userStorage.createUser(nextUser);
        User nextFriend = new User(0, "pimkPig@mail.ru", "nextFriend", "Pinkman", LocalDate.of(1986, 2, 10));
        userStorage.createUser(nextFriend);
        long userId = newUser.getId();
        long friendId = nextUser.getId();
        long friendId2 = nextFriend.getId();
        friendStorage.addFriend(userId, friendId);
        friendStorage.addFriend(userId, friendId2);
        assertEquals(2, friendStorage.getListOfFriends(userId).size());
        friendStorage.removeFriend(userId, friendId);
        assertEquals(1, friendStorage.getListOfFriends(userId).size());
    }

    @Test
    public void findAllCommonFriendsTest() {
        User newUser = new User(0, "alex@mail.ru", "newuser", "Alex", LocalDate.of(1985, 4, 19));
        userStorage.createUser(newUser);
        User nextUser = new User(0, "piterPen@mail.ru", "nextuser", "Piter", LocalDate.of(1987, 3, 12));
        userStorage.createUser(nextUser);
        User nextFriend = new User(0, "pimkPig@mail.ru", "nextFriend", "Pinkman", LocalDate.of(1986, 2, 10));
        userStorage.createUser(nextFriend);
        long userId = newUser.getId();
        long friendId = nextUser.getId();
        long friendId2 = nextFriend.getId();
        friendStorage.addFriend(userId, friendId);
        friendStorage.addFriend(userId, friendId2);
        friendStorage.addFriend(friendId, friendId2);
        assertEquals(1, friendStorage.findAllCommonFriends(userId, friendId).size());
    }
*/
}
