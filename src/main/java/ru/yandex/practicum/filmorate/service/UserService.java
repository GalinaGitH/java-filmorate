package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;


    /**
     * сохранение пользователя
     */
    public User saveUser(User user) {
        final User userInStorage = userStorage.get(user.getId());
        if (userInStorage == null) {
            String name = user.getName();
            if (name.isBlank()) {
                user.setName(user.getLogin());
            }
            userStorage.createUser(user);
        } else throw new AlreadyExistException(String.format(
                "Пользователь с таким id %s уже зарегистрирован.", user.getId()));
        return user;
    }

    /**
     * изменение пользователя
     */
    public User updateUser(User user) {
        final User userInStorage = userStorage.get(user.getId());
        if (userInStorage == null) {
            throw new NotFoundException("User with id=" + user.getId() + "not found");
        }
        userStorage.update(user);
        return user;
    }

    /**
     * даление пользователя
     */
    public void delete(User user) {
        userStorage.remove(user);
    }

    /**
     * получение списка всех пользователей
     */
    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    /**
     * получение пользователя по id
     */
    public User get(long userId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException("User with id=" + userId + "not found");
        }
        return user;
    }
}
