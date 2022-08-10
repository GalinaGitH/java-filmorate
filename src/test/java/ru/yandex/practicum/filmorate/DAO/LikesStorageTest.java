package ru.yandex.practicum.filmorate.DAO;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikesStorageTest {
    private final LikesDbStorage likesStorage;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;


    @Test
    public void addLikesAndFindPopularFilmTest() {
        User newUser = new User(0, "user@mail.ru", "testuser", "Mikl", LocalDate.of(1990, 8, 12));
        userStorage.createUser(newUser);
        Film nextfilm = new Film(0, "labore nulla", LocalDate.of(1979, 04, 17), "Duis in consequat esse", 100);
        nextfilm.setMpa(mpaStorage.getById(1));
        filmStorage.create(nextfilm);
        likesStorage.addLikes(nextfilm.getId(), newUser.getId());
        assertEquals(1, likesStorage.findPopularFilm(2).size());
    }
}

