package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film create(Film film) throws FilmReleaseException;

    Film update(Film film) throws FilmNotFoundException;

    Film putLike(int id, int userId);

    Film deleteLike(int id, int userId);

    Film getFilm(int id);

    List<Film> findPopularFilms(int count);
}