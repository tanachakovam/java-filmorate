package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private int id;


    @GetMapping("/users")
    public Map<Integer, User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.debug("Получен запрос POST /user.");
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
        } else {
            user.setId(++id);
            users.put(user.getId(), user);
        }
        log.debug("Получен запрос PUT /user.");
        return user;
    }
}
