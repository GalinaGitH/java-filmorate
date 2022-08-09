package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendsService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Validated
@RestController
@NoArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private FriendsService friendsService;

    @Autowired
    public UserController(UserService userService, FriendsService friendsService) {
        this.userService = userService;
        this.friendsService = friendsService;
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
        friendsService.addFriend(userId, friendId);
        log.debug("Добавлен еще один друг");
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        friendsService.removeFriend(userId, friendId);
        log.debug("Друг с id= {} удален из списка", friendId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable long userId, @PathVariable long otherId) {
        log.debug("Количество общих друзей: {}", friendsService.findAllCommonFriends(userId, otherId).size());
        return friendsService.findAllCommonFriends(userId, otherId);
    }

    @GetMapping("/users/{userId}/friends")
    public Collection<User> findAllFriends(@PathVariable long userId) {
        log.debug("Количество друзей: {}", friendsService.getListOfFriends(userId).size());
        return friendsService.getListOfFriends(userId);
    }
}

