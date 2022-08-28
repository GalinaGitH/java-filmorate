package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FriendsService {
    private final FriendStorage friendStorage;
    private final UserStorage userStorage;

    private final FeedService feedService;

    /**
     * добавление в друзья
     * Условие:
     */
    public void addFriend(long userId, long friendId) {

        userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userStorage
                .get(friendId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        feedService.addFriendInFeed(userId, friendId);
        friendStorage.addFriend(userId, friendId);
    }

    /**
     * удаление из друзей
     */
    public void removeFriend(long userId, long friendId) {

        userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userStorage
                .get(friendId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        feedService.removeFriendInFeed(userId, friendId);
        friendStorage.removeFriend(userId, friendId);
    }

    /**
     * вывод списка общих друзей
     */
    public List<User> findAllCommonFriends(long userId, long friendId) {
        return friendStorage.findAllCommonFriends(userId, friendId);
    }

    /**
     * вывод списка друзей пользователя
     */
    public List<User> getListOfFriends(long userId) {

        userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return friendStorage.getListOfFriends(userId);
    }
}
