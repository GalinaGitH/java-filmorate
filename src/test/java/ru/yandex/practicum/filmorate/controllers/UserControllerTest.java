package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Validated
class UserControllerTest {
    UserController uController;

    @BeforeEach
    void init() {
        uController = new UserController();
    }

    @Test
    public void createUserTest() {
        User newUser = new User(0, "user@mail.ru", "testuser", "Mikl", LocalDate.of(1990, 8, 12));
        uController.createUser(newUser);
        assertEquals(newUser.getId(), 1);
    }

    @Test
    public void createUserFailNameTest() {
        User newUser = new User(0, "user@mail.ru", "testuser", " ", LocalDate.of(1990, 8, 12));
        uController.createUser(newUser);
        assertEquals(newUser.getId(), 1);
        assertTrue(newUser.getName().equals("testuser"));
    }

    /**
     * Принудительная валидация
     */
    @Test
    public void manualValidationFailBirthdayTest() {
        User newUser = new User(0, "user@mail.ru", "testuser", "Kate ", LocalDate.of(2023, 1, 5));
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        ConstraintViolation<User> violation = violations.stream().findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));
        assertEquals("дата рождения не может быть в будущем", violation.getMessageTemplate());
    }

    @Test
    public void manualValidationFailEmailTest() {
        User newUser = new User(0, "mail.ru", "newuser", "Leo", LocalDate.of(2000, 1, 5));
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        ConstraintViolation<User> violation = violations.stream().findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));
        assertEquals("Email должен быть корректным адресом электронной почты", violation.getMessageTemplate());
    }

    @Test
    public void manualValidationFailLoginTest() {
        User newUser = new User(0, "newuser@mail.ru", " ", "Leo", LocalDate.of(2000, 1, 5));
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        ConstraintViolation<User> violation = violations.stream().findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));
        assertEquals("логин не может быть пустым и содержать пробелы", violation.getMessageTemplate());
    }

    @Test
    public void updateUserTest() {
        User newUser = new User(0, "user@mail.ru", "testuser", "Mikl", LocalDate.of(1990, 8, 12));
        uController.createUser(newUser);
        newUser = new User(1, "user@mail.ru", "olduser", "Mikhail", LocalDate.of(1990, 8, 12));
        uController.update(newUser);
        assertEquals(newUser.getLogin(), "olduser");
    }

    @Test
    public void updateNotFoundUserTest() {
        final RuntimeException ex = assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() {
                User newUser = new User(0, "user@mail.ru", "testuser", "Mikl", LocalDate.of(1990, 8, 12));
                uController.createUser(newUser);
                newUser = new User(2, "user@mail.ru", "olduser", "Mikhail", LocalDate.of(1990, 8, 12));
                uController.update(newUser);
            }
        });
        assertNotNull(ex.getMessage());
    }

    @Test
    public void findAllUsersTest() {
        User newUser = new User(0, "alex@mail.ru", "newuser", "Alex", LocalDate.of(1985, 4, 19));
        uController.createUser(newUser);
        User nextUser = new User(0, "piterPen@mail.ru", "nextuser", "Piter", LocalDate.of(1987, 3, 12));
        uController.createUser(nextUser);
        assertEquals(2, uController.findAllUsers().size());
    }
}