package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int id;


    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }


    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }


    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User get(int userId) {
        return users.get(userId);
    }

    public void addFriend(User user, User friend) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        user.getFriends().add(friend.getId());

        friend.getFriends().add(user.getId());

    }

    public void deleteFriend(User user, User friend) {
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public List<User> getAllFriends(User user) {
        List<User> friends = new ArrayList<>();
        for (int id : user.getFriends()) {
            friends.add(users.get(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(User user, User other) {
        List<User> commonFriends = new ArrayList<>();
        for (int id : user.getFriends()) {
            if (other.getFriends().contains(id)) {
                commonFriends.add(users.get(id));
            }
        }
        return commonFriends;
    }
}
