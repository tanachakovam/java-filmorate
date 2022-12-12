package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;


import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.sql.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Qualifier
@Repository
public class FilmDbStorage implements FilmStorage {
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 18);
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> findAll() {
        String sqlQuery = "select ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, m.MPA_ID, m.mpa_name " +
                " from films AS f " +
                "JOIN MPA AS m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    public Film create(Film film) throws FilmReleaseException {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new FilmReleaseException("Incorrect release date.");
        }
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        simpleJdbcInsert.execute(values);
        saveGenre(film);
        return film;
    }


    public void saveGenre(Film film) {
        String sqlQuery = "Delete from film_genres " +
                "where film_id = ? ";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            String sqlQuery1 = "insert into film_genres(film_id, genre_id) " +
                    "values (?,?)";
            jdbcTemplate.update(sqlQuery1, film.getId(), genre.getId());
        }

    }

    public Film update(Film film) throws FilmNotFoundException {
        String sqlQuery = "update films set " +
                "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getId());
        return film;
    }

    public Film getFilm(int id) {
        String sqlQuery = "select ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, m.MPA_ID, m.mpa_name " +
                " from films AS f " +
                "JOIN MPA AS m ON f.mpa_id = m.mpa_id " +
                "where id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);
        if (films.size() != 1) {
            return null;
        }
        return films.get(0);
    }

    public Film putLike(int id, int userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes");
        Map<String, Object> values = new HashMap<>();
        values.put("film_id", id);
        values.put("user_id", userId);
        simpleJdbcInsert.execute(values);
        return getFilm(id);
    }

    public Film deleteLike(int id, int userId) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return getFilm(id);
    }

    public List<Film> findPopularFilms(int count) {
        String sqlQuery = "select ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, m.MPA_ID, m.mpa_name " +
                " from films AS f " +
                "JOIN MPA AS m ON f.mpa_id = m.mpa_id " +
                "join likes as l ON f.id = l.film_id " +
                "group by FILM_NAME, DESCRIPTION,RELEASE_DATE, DURATION, m.MPA_ID, m.mpa_name " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("MPA.mpa_name")))
                .genres(getGenreById(rs.getInt("id")))
                .build();
    }


    private static Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("MPA.mpa_name"))
                .build();
    }

    private static Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    public Mpa getMpaById(int id) {
        String sqlQuery = "select m.MPA_ID, MPA_NAME " +
                "from MPA AS m " +
                "join Films AS f ON m.MPA_ID = f.MPA_ID " +
                "where f.ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, FilmDbStorage::mapRowToMpa, id);
    }

    public List<Mpa> getAllMpa() {
        String sqlQuery = "select * from MPA";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToMpa);
    }

    public List<Genre> getGenreById(int id) {
        String sqlQuery = "select fg.genre_id, g.GENRE_NAME " +
                "from film_genres AS fg " +
                "join GENRES as g ON fg.GENRE_ID = g.GENRE_ID " +
                "where fg.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToGenre, id);
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "select * from GENRES";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToGenre);
    }
}
