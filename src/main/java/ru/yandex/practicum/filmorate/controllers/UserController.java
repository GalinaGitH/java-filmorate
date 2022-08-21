package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Validated
@RestController
@NoArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private FriendsService friendsService;
    private FeedService feedService;

    private LikesService likesService;

    private FilmService filmService;

    @Autowired
    public UserController(UserService userService, FriendsService friendsService, LikesService likesService,
                          FilmService filmService, FeedService feedService) {
        this.userService = userService;
        this.friendsService = friendsService;
        this.likesService = likesService;
        this.filmService = filmService;
        this.feedService = feedService;
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

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") long userId) {
        userService.deleteUserById(userId);
        log.debug("Пользователь с id= {} удален из списка", userId);
    }


    @GetMapping("/users/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable long id) {
        List<Film> recFilms = filmService.getRecommended(id);
        log.debug("Количество рекомендованных фильмов: {}", recFilms.size());
        return recFilms;
    }
}

