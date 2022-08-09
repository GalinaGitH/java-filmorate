package ru.yandex.practicum.filmorate.DAO;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {
    /*private final FilmDbStorage filmStorage;
    private final MpaDbStorage  mpaStorage;


    @Test
    public void createFilmAndGetFilmTest() {
        Film nextfilm = new Film(0,"labore nulla",  LocalDate.of(1979, 04,17), "Duis in consequat esse", 100);
        nextfilm.setMpa(mpaStorage.getById(1));
        filmStorage.create(nextfilm);
        assertEquals(filmStorage.get(1).getId(),1);
    }

    @Test
    public void updateFilmTest() {
        Film film = new Film(0,"labore nulla",  LocalDate.of(1979, 04,17), "Duis in consequat esse", 100);
        film.setMpa(mpaStorage.getById(1));
        filmStorage.create(film);
        film.setName("SomeThingNew");
        filmStorage.update(film);
        assertEquals(film.getName(), "SomeThingNew");
    }*/
/*

    @Test
    public void removeFilmAndFindAllTest() {
        Film film = new Film(0,"labore nulla",  LocalDate.of(1979, 04,17), "Duis in consequat esse", 100);
        film.setMpa(mpaStorage.getById(1));
        filmStorage.create(film);
        Film newFilm = new Film(2, "Dinosaur", LocalDate.of(2022, 10, 10), "fantastic, adventure,animated short film by American " +
                "\n ", 120);
        newFilm.setMpa(mpaStorage.getById(1));
        filmStorage.create(newFilm);
        assertEquals(2, filmStorage.findAll().size());
        filmStorage.remove(newFilm);
        assertEquals(1, filmStorage.findAll().size());
    }
*/

}
