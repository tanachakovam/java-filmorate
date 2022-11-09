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
        log.info("Получен запрос GET /users. Текущее количество пользователей: {}.", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Получен запрос POST /users. Создан пользователь {}.", user.getName());
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws Exception {
        if (!users.containsKey(user.getId())) {
            throw new Exception("User with this ID doesn't exist.");
        }
        users.put(user.getId(), user);
        log.info("Получен запрос PUT /users. Данные пользователя {} обновлены.", user.getName());
        return user;
    }
}
