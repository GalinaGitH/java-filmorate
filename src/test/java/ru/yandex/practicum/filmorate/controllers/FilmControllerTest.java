package ru.yandex.practicum.filmorate.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Тестирование контроллера с помощью MockMvc(P.S. это новая пока не изученная тема).
 * P.S. класс UserController протестировала с помощью unit тестов
 */

/*
@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createFilmTest() throws Exception {
        Film filmOne = new Film(0, "Avatar", LocalDate.of(2009, 12, 10), "fantastic, adventure", 100);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmOne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Avatar"));
    }

    @Test
    public void updateFilmTest() throws Exception {
        Film filmOne = new Film(0, "Avatar", LocalDate.of(2009, 12, 10), "fantastic, adventure", 100);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmOne)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Film(1, "Avatar1", LocalDate.of(2009, 12, 10), "fantastic, adventure", 162))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Avatar1"))
                .andExpect(jsonPath("$.duration").value("162"));
    }

    @Test
    public void findAllFilmTest() throws Exception {
        Film filmOne = new Film(0, "Avatar1", LocalDate.of(2009, 12, 10), "fantastic, adventure", 162);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmOne)))
                .andExpect(status().isOk());

        Film filmTwo = new Film(0, "Avatar2", LocalDate.of(2022, 11, 14), "fantastic, adventure,joney", 180);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmTwo)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andExpect(status().isOk());
    }

    */
/**
     * Принудительная валидация
     *//*

    @Test
    public void manualValidationFailFilmNameTest() {
        Film newFilm = new Film(0, " ", LocalDate.of(2009, 12, 10), "fantastic, adventure", 162);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        ConstraintViolation<Film> violation = violations.stream().findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));
        assertEquals("название не может быть пустым и содержать пробелы", violation.getMessageTemplate());
    }

    @Test
    public void manualValidationFailReleaseDateTest() {
        Film newFilm = new Film(0, "Dinosaur", LocalDate.of(1890, 10, 10), "fantastic, adventure", 150);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        ConstraintViolation<Film> violation = violations.stream().findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));
        assertEquals("дата релиза — не раньше {value}", violation.getMessageTemplate());
    }

    @Test
    public void manualValidationFailDurationTest() {
        Film newFilm = new Film(0, "Dinosaur", LocalDate.of(2022, 10, 10), "fantastic, adventure", -100);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        ConstraintViolation<Film> violation = violations.stream().findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));
        assertEquals("продолжительность фильма должна быть положительной", violation.getMessageTemplate());
    }

    @Test
    public void manualValidationFailDescriptionTest() {
        Film newFilm = new Film(0, "Dinosaur", LocalDate.of(2022, 10, 10), "fantastic, adventure,animated short film by American cartoonist and animator Winsor McCay. It is the earliest animated film to feature a dinosaur. Were it not for Jurassic World: Fallen Kingdom, this would be the worst Jurassic movie. Another attempt to deviate from a perfect story, The Lost World fails on many fronts. Half the original cast is missing (replaced by Vince Vaughn and others), all the characters know exactly what to expect from the island, and the finale (in which a T rex goes nuts in San Diego) sails far too close to pastiche.\n" +
                "\n ", 120);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        ConstraintViolation<Film> violation = violations.stream().findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));
        assertEquals("максимальная длина описания — 200 символов", violation.getMessageTemplate());
    }

}

*/

@Validated
class FilmControllerTest {

    FilmService filmService;
    InMemoryFilmStorage inMemoryFilmStorage;
    FilmController fController;

    @BeforeEach
    void init() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(inMemoryFilmStorage);
        fController = new FilmController(filmService);
    }

    @Test
    public void createFilmTest() {
        Film film= new Film("labore nulla",  LocalDate.of(1979, 04,17), "Duis in consequat esse", 100);
        fController.create(film);
        assertEquals(film.getId(), 1);
    }
}


