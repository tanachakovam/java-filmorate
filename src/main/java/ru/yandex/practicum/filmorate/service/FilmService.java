package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    final
    FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) throws FilmReleaseException {
        return filmStorage.create(film);
    }

    public Film update(Film film) throws FilmNotFoundException {
        return filmStorage.update(film);
    }

    public Film putLike(int id, int userId) {
        return filmStorage.putLike(id, userId);
    }

    public Film deleteLike(int id, int userId) {
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.findPopularFilms(count);
    }
}