package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;


    /**
     * сохранение пользователя
     */
    public User saveUser(User user) {

        userStorage
                .get(user.getId())
                .ifPresent((val) -> {
                            throw new AlreadyExistException(String.format("Пользователь с таким id %s" +
                                    " уже зарегистрирован.", user.getId()));
                        }
                );

        String name = user.getName();
        if (name.isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.createUser(user);

        return user;
    }

    /**
     * изменение пользователя
     */
    public User updateUser(User user) {

        userStorage
                .get(user.getId())
                .orElseThrow(() -> new NotFoundException("User with id=" + user.getId() + "not found"));

        userStorage.update(user);

        return user;
    }

    /**
     * получение списка всех пользователей
     */
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    /**
     * получение пользователя по id
     */
    public User get(long userId) {

        return userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + "not found"));
    }

    /**
     * удаление пользователя по id
     */
    public void deleteUserById(long userId) {

        userStorage
                .get(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + "not found"));

        userStorage.removeUserById(userId);
    }

}
