package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 18);
    private Map<Integer, Film> films = new HashMap<>();
    private int id;


    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }


    public Film create(Film film) throws FilmReleaseException {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new FilmReleaseException("Incorrect release date.");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) throws FilmNotFoundException {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Film with this ID doesn't exist.");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film getFilm(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Film with this ID doesn't exist.");
        }
        return films.get(id);
    }


    public Film putLike(int id, int userId) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Film with this ID doesn't exist.");
        }
        films.get(id).getLikes().add(userId);
        return films.get(id);
    }

    public Film deleteLike(int id, int userId) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Film with this ID doesn't exist.");
        }
        if (userId <= 0) {
            throw new UserNotFoundException("User with this ID doesn't exist.");
        }
        films.get(id).getLikes().remove(userId);
        return films.get(id);
    }

    public List<Film> findPopularFilms(int count) {
        return films.values().stream().sorted((f0, f1) -> {
            int comp = f1.getLikes().size() - f0.getLikes().size();
            return comp;
        }).limit(count).collect(Collectors.toList());
    }
}
