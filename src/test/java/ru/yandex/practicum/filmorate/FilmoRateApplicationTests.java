package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = Optional.ofNullable(userStorage.get(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindFilmById() {

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilm(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindFilms() {

        Optional<List<Film>> filmsOptional = Optional.ofNullable(filmStorage.findAll());
        assertThat(filmsOptional)
                .isPresent()
                .hasValueSatisfying(films ->
                        assertThat(films).hasSizeGreaterThan(0)
                );
    }

    @Test
    public void testFindUsers() {

        Optional<List<User>> usersOptional = Optional.ofNullable(userStorage.findAll());
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(users ->
                        assertThat(users).hasSizeGreaterThan(0)
                );
    }

    @Test
    public void testFindMpas() {

        Optional<List<Mpa>> mpaOptional = Optional.ofNullable(filmStorage.getAllMpa());
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpas ->
                        assertThat(mpas).hasSizeLessThanOrEqualTo(5)
                );
    }

    @Test
    public void testFindGenres() {

        Optional<List<Genre>> genreOptional = Optional.ofNullable(filmStorage.getAllGenres());
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genres ->
                        assertThat(genres).hasSizeLessThanOrEqualTo(6)
                );
    }
}