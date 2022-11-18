package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
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
        films.get(id).getLikes().remove(userId);
        return films.get(id);
    }

    public List<Film> findPopularFilms(int count) {
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
