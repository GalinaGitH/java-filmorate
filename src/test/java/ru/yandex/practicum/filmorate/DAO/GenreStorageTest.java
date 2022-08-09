package ru.yandex.practicum.filmorate.DAO;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreStorageTest {
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;

    @Test
    public void getGenreByIdTest() {
        Genre genre = genreStorage.getById(1);
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void getGenreByIdTest2() {
        Genre genre = genreStorage.getById(2);
        assertEquals("Драма", genre.getName());
    }

    @Test
    public void getAllGenresTest() {
        assertEquals(6, genreStorage.getAll().size());
    }

}
