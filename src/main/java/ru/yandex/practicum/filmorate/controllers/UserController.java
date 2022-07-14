package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Validated
@RestController
@NoArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", userService.findAllUsers().size());
        return userService.findAllUsers();
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable long userId) {
        log.info("Get user by id={}", userId);
        return userService.get(userId);
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        userService.saveUser(user);
        log.debug("количество добавленных пользователей: {}", 1);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        userService.updateUser(user);
        log.debug("Данные пользователя обновлены");
        return user;
    }

    @PutMapping("/users/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.addFriend(userId, friendId);
        log.debug("Добавлен еще один друг");
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.removeFriend(userId, friendId);
        log.debug("Друг с id= {} удален из списка", friendId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable long userId, @PathVariable long otherId) {
        log.debug("Количество общих друзей: {}", userService.findAllCommonFriends(userId, otherId).size());
        return userService.findAllCommonFriends(userId, otherId);
    }

    @GetMapping("/users/{userId}/friends")
    public Collection<User> findAllFriends(@PathVariable long userId) {
        log.debug("Количество друзей: {}", userService.getListOfFriends(userId).size());
        return userService.getListOfFriends(userId);
    }
}

