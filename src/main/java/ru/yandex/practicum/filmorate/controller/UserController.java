package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("users")
public class UserController {
    final
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    List<User> getAllUsers() {
        log.info("Получен запрос GET /users. Список всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable int userId) {
        log.info("Найдем пользователя по id = " + userId + ".");
        return userService.get(userId);
    }

    @PostMapping
    User create(@Valid @RequestBody User user) {
        log.info("Получен запрос POST /users. Создан пользователь {}.", user.getName());
        return userService.create(user);
    }

    @PutMapping
    User update(@RequestBody User user) {
        log.info("Получен запрос PUT /users. Данные пользователя {} обновлены.", user.getName());
        return userService.update(user);
    }

//PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Добавление в друзья.");
        userService.addFriend(id, friendId);
    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Удаление из друзей.");
        userService.deleteFriend(id, friendId);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable int id) {
        log.info("Возвращаем список пользователей, являющихся друзьями.");
        return userService.getAllFriends(id);
    }

    // GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Возвращаем список друзей, общих с другим пользователем");
        return userService.getCommonFriends(id, otherId);
    }
}
