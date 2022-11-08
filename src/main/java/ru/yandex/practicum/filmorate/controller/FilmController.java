package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.MyValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 18);
    private Map<Integer, Film> films = new HashMap<>();
    private int id;


    @GetMapping("/films")
    public Map<Integer, Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) throws MyValidationException {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new MyValidationException("Incorrect release date.");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Получен запрос POST /film.");
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
        } else {
            film.setId(++id);
            films.put(film.getId(), film);
        }
        log.debug("Получен запрос PUT /film.");
        return film;
    }
}





