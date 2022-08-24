package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User update(User user);

    void remove(User user);

    User get(long userId);

    List<User> findAllUsers();

    void removeUserById(long userId);
}
