package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;


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
        final User user = userStorage.get(userId);
        final User friend = userStorage.get(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("User  not found");
        }
        feedService.addFriendInFeed(userId, friendId);
        friendStorage.addFriend(userId, friendId);
    }

    /**
     * удаление из друзей
     */
    public void removeFriend(long userId, long friendId) {
        final User user = userStorage.get(userId);
        final User friend = userStorage.get(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("User  not found");
        }
        feedService.removeFriendInFeed(userId, friendId);
        friendStorage.removeFriend(userId, friendId);
    }

    /**
     * вывод списка общих друзей
     */
    public Collection<User> findAllCommonFriends(long userId, long friendId) {
        return friendStorage.findAllCommonFriends(userId, friendId);
    }

    /**
     * вывод списка друзей пользователя
     */
    public Collection<User> getListOfFriends(long userId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException("User with id=" + userId + "not found");
        }
        return friendStorage.getListOfFriends(userId);
    }
}
