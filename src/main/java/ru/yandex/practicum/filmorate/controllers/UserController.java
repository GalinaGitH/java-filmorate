package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FriendsService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final FriendsService friendsService;
    private final FeedService feedService;
    private final FilmService filmService;

    @GetMapping("/users")
    public List<User> findAllUsers() {
        log.debug("Текущее количество пользователей: {}", userService.findAllUsers().size());
        return userService.findAllUsers();
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable long userId) {
        log.info("Get user by id={}", userId);
        return userService.get(userId);
    }

    @GetMapping("/users/{userId}/feed")
    public List<Feed> getFeeds(@PathVariable int userId) {
        log.info("Get feeds user by id={}", userId);
        return feedService.getFeeds(userId);
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        userService.saveUser(user);
        log.debug("количество добавленных пользователей: {}", 1);
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
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
    public List<User> findCommonFriends(@PathVariable long userId, @PathVariable long otherId) {
        log.debug("Количество общих друзей: {}", friendsService.findAllCommonFriends(userId, otherId).size());
        return friendsService.findAllCommonFriends(userId, otherId);
    }

    @GetMapping("/users/{userId}/friends")
    public List<User> findAllFriends(@PathVariable long userId) {
        log.debug("Количество друзей: {}", friendsService.getListOfFriends(userId).size());
        return friendsService.getListOfFriends(userId);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") long userId) {
        userService.deleteUserById(userId);
        log.debug("Пользователь с id= {} удален из списка", userId);
    }


    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendedFilms(@PathVariable long id) {
        List<Film> recFilms = filmService.getRecommended(id);
        log.debug("Количество рекомендованных фильмов: {}", recFilms.size());
        return recFilms;
    }
}

