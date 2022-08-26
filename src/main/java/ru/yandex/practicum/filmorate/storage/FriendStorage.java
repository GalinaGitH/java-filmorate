package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> findAllCommonFriends(long userId, long friendId);

    List<User> getListOfFriends(long userId);
}
