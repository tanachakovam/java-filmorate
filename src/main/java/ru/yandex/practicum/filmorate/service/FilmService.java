package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

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
        if (filmStorage.getFilm(film.getId()) == null) {
            throw new FilmNotFoundException("Film with this ID doesn't exist.");
        }
        return filmStorage.update(film);
    }

    public Film getFilm(int id) {
        if (filmStorage.getFilm(id) == null) {
            throw new FilmNotFoundException("Film doesn't exist.");
        }
        return filmStorage.getFilm(id);
    }

    public Film putLike(int id, int userId) {
        return filmStorage.putLike(id, userId);
    }

    public Film deleteLike(int id, int userId) {
        if (filmStorage.getFilm(id) == null) {
            throw new FilmNotFoundException("Film doesn't exist.");
        }
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.findPopularFilms(count);
    }

    public Mpa getMpaById(int id) {
        if (filmStorage.getFilm(id) == null) {
            throw new FilmNotFoundException("Film doesn't exist.");
        }
        return filmStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public List<Genre> getGenreById(int id) {
        if (filmStorage.getFilm(id) == null) {
            throw new FilmNotFoundException("Film doesn't exist.");
        }
        return filmStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }
}