package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
public class FilmController {

    final
    FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    List<Film> findAll() {
        log.info("Получен запрос GET /films.");
        return filmService.findAll();
    }

    @PostMapping(value = "/films")
    Film create(@Valid @RequestBody Film film) throws FilmReleaseException {
        log.info("Получен запрос POST /films. Фильм {} добавлен.", film.getName());
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    Film update(@RequestBody Film film) {
        log.info("Получен запрос PUT /films. Фильм {} обновлен.", film.getName());
        return filmService.update(film);
    }

    // PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    @PutMapping("/films/{id}/like/{userId}")
    Film putLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Пользователь ставит лайк фильму.");
        return filmService.putLike(id, userId);
    }

    //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    @DeleteMapping("/films/{id}/like/{userId}")
    Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Пользователь удаляет лайк.");
        return filmService.deleteLike(id, userId);
    }

    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.
    @GetMapping("/films/popular")
    List<Film> findPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        log.info("возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.");
        return filmService.findPopularFilms(count);
    }
}





