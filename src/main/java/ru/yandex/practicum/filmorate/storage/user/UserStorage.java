package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    User get(int userId);

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<User> getAllFriends(User user);

    List<User> getCommonFriends(User user, User other);
}
