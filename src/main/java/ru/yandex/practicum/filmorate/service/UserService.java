package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        if (userStorage.get(user.getId()) == null) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        return userStorage.update(user);
    }

    public User get(int userId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найденю");
        }
        return user;
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.get(userId);
        if (friendId <= 0 || userStorage.get(friendId) == null) {
            throw new UserNotFoundException("Friend with this ID doesn't exist.");
        }
        User friend = userStorage.get(friendId);
        userStorage.addFriend(user, friend);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);
        userStorage.deleteFriend(user, friend);
    }

    public List<User> getAllFriends(int userId) {
        User user = userStorage.get(userId);
        return userStorage.getAllFriends(user);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.get(userId);
        User other = userStorage.get(otherId);
        return userStorage.getCommonFriends(user, other);
    }
}
