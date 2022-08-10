package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendStorage {
    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    Collection<User> findAllCommonFriends(long userId, long friendId);

    public Collection<User> getListOfFriends(long userId);
}
