package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User update(User user);

    void remove(User user);

    Optional<User> get(long userId);

    List<User> findAllUsers();

    void removeUserById(long userId);
}
