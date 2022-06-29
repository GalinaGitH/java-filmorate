package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Validated
@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int nextId = 0;

    private int getNextId() {
        return ++nextId;
    }

    @GetMapping("/users")
    public Collection<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        user.setId(getNextId());
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("количество добавленных пользователей: {}", 1);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            RuntimeException e = new ValidationException("Пользователь с таким Id не найден");
            log.error(e.getMessage());
            throw e;
        }
        users.put(user.getId(), user);
        log.debug("Данные пользователя обновлены");
        return user;
    }

}

