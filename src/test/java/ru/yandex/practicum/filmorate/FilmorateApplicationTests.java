package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.MyValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {
	FilmController filmController;
	UserController userController;
	Film film;
	User user;

	@Test
	void checkFilmValidation() throws ru.yandex.practicum.filmorate.exception.MyValidationException {
		filmController = new FilmController();
		film =  Film.builder()
				.name("Film 1")
				.description("Film 1 description")
				.releaseDate(LocalDate.of(2022,11,1))
				.duration(23)
				.build();
		filmController.create(film);
		assertEquals(1, filmController.findAll().size());
	}

	@Test
	void checkFilmReleaseDate() {
		filmController = new FilmController();
		film =  Film.builder()
				.name("name")
				.description("Film 1 description")
				.releaseDate(LocalDate.of(1322,11,1))
				.duration(23)
				.build();
		MyValidationException exc = assertThrows(
				MyValidationException.class, () -> filmController.create(film)
		);
		Assertions.assertEquals("Incorrect release date.", exc.getMessage());
	}

	@Test
	void checkUserValidation() {
		userController = new UserController();
		user =  User.builder()
				.email("email@mail.ru")
				.login("login")
				.name("name")
				.birthday(LocalDate.of(2022,11,1))
				.build();
		userController.create(user);
		assertEquals(1, filmController.findAll().size());
	}
}
