package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private int id;


    @GetMapping("/users")
    public List<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
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
