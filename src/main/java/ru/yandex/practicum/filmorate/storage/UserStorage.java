package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User createUser(User user);

    User update(User user);

    void remove(User user);

    User get(long userId);

    Collection<User> findAllUsers();
}
