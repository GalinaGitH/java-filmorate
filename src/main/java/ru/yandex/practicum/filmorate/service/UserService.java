package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * сохранение пользователя
     */
    public User saveUser(User user) {
        final User userInStorage = userStorage.get(user.getId());
        if (userInStorage == null) {
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
     * удаление пользователя
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
     * добавление в друзья
     * Условие:если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
     */
    public void addFriend(long userId, long friendId) {
        final User user = userStorage.get(userId);
        final User friend = userStorage.get(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("User  not found");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    /**
     * удаление из друзей
     */
    public void removeFriend(long userId, long friendId) {
        final User user = userStorage.get(userId);
        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            final User friend = userStorage.get(friendId);
            friend.getFriends().remove(userId);
        } else throw new NotFoundException("User with id=" + friendId + "not found in friendsList");
    }

    /**
     * вывод списка общих друзей
     */
    public Collection<User> findAllCommonFriends(long userId, long friendId) {
        Set<Long> friendsSet1 = userStorage.get(userId).getFriends();
        Set<Long> friendsSet2 = userStorage.get(friendId).getFriends();
        Collection<User> commonSet = new ArrayList<>();
        for (Long id : friendsSet1) {
            if (friendsSet2.contains(id)) {
                User commonFriend = userStorage.get(id);
                commonSet.add(commonFriend);
            }
        }
        return commonSet;
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

    /**
     * вывод списка друзей пользователя
     */
    public Collection<User> getListOfFriends(long userId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException("User with id=" + userId + "not found");
        }
        Set<Long> friendsId = user.getFriends();
        Collection<User> friends = new ArrayList<>();
        for (Long id : friendsId) {
            User usersFriend = userStorage.get(id);
            friends.add(usersFriend);
        }
        return friends;
    }
}
