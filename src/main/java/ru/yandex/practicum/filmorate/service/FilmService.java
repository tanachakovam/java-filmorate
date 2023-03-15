package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;


    public FilmService(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
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
        return mpaStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Genre getGenreById(int id) {
        if (filmStorage.getFilm(id) == null) {
            throw new FilmNotFoundException("Genre doesn't exist.");
        }
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }
}