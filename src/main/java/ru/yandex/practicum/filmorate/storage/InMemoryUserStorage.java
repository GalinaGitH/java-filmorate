package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new LinkedHashMap<>();
    private long nextId = 0;

    private long getNextId() {
        return ++nextId;
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void remove(User user) {
        users.remove(user.getId());
    }

    public Collection<User> findAllUsers() {
        return users.values();
    }

    public User get(long userId) {
        return users.get(userId);
    }
}
