package ru.yandex.practicum.filmorate.DAO;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
/*

    @AfterEach
    void cleanupAfterEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "USERS");
    }

    @Test
    public void createUserAndRemoveTest() {
        User newUser = new User(0, "user@mail.ru", "testuser", "Mikl", LocalDate.of(1990, 8, 12));
        userStorage.createUser(newUser);
        User newUser2 = new User(0, "user@mail.ru", "testuser", "Mikl", LocalDate.of(1990, 8, 12));
        userStorage.createUser(newUser2);
        assertEquals(2, userStorage.findAllUsers().size());
        userStorage.remove(newUser);
        assertEquals(1, userStorage.findAllUsers().size());
    }

    @Test
    public void testFindUserById() {
        User newUser = new User(0, "user@mail.ru", "testuser", "Mikl", LocalDate.of(1990, 8, 12));
        userStorage.createUser(newUser);
        long id = newUser.getId();
        assertThat(newUser.getId()).isEqualTo(id);

    }

    @Test
    public void updateUserTest() {
        User newUser = new User(0, "user@mail.ru", "testuser", "Mikl", LocalDate.of(1990, 8, 12));
        userStorage.createUser(newUser);
        newUser = new User(1, "user@mail.ru", "olduser", "Mikhail", LocalDate.of(1990, 8, 12));
        userStorage.update(newUser);
        assertEquals(newUser.getLogin(), "olduser");
    }

    @Test
    public void findAllUsersTest() {
        User newUser = new User(0, "alex@mail.ru", "newuser", "Alex", LocalDate.of(1985, 4, 19));
        userStorage.createUser(newUser);
        User nextUser = new User(0, "piterPen@mail.ru", "nextuser", "Piter", LocalDate.of(1987, 3, 12));
        userStorage.createUser(nextUser);
        assertEquals(2, userStorage.findAllUsers().size());
    }
*/

}
