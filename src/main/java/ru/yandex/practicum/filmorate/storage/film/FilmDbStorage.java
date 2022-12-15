package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;


import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.sql.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Qualifier
@Repository
public class FilmDbStorage implements FilmStorage {
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 18);
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> findAll() {
        String sqlQuery = "select ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID " +
                "from films AS f";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    public Film create(Film film) throws FilmReleaseException {
        String sqlQuery = "insert into FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
                try {
                    throw new FilmReleaseException("Incorrect release date.");
                } catch (FilmReleaseException e) {
                    throw new RuntimeException(e);
                }
            }
            final LocalDate releaseDate = film.getReleaseDate();
            if (releaseDate == null) {
                stmt.setNull(3, Types.DATE);
            } else {
                stmt.setDate(3, Date.valueOf(releaseDate));
            }
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
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
        List<Genre> uniqueGenres = film.getGenres().stream().distinct().collect(Collectors.toList());
        for (Genre genre : uniqueGenres) {
            String sqlQuery1 = "insert into film_genres(film_id, genre_id) " +
                    "values (?,?)";
            jdbcTemplate.update(sqlQuery1, film.getId(), genre.getId());
        }
    }

    public Film update(Film film) throws FilmNotFoundException {
        String sqlQuery = "update films set " +
                "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        saveGenre(film);
        return getFilm(film.getId());
    }

    public Film getFilm(int id) {
        String sqlQuery = "select ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID " +
                " from films AS f " +
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
        if (userId < 0) {
            throw new UserNotFoundException("User doesn't exist.");
        }
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return getFilm(id);
    }

    public Film deleteLikeAll(int id) {
        String sqlQuery = "delete from likes where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
        return getFilm(id);
    }

    public List<Film> findPopularFilms(int count) {
        String sqlQuery = "select ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, f.MPA_ID, COUNT(l.FILM_ID) AS likes" +
                " from films AS f " +
                "join mpa as m ON f.MPA_ID = m.MPA_ID " +
                "left join FILM_GENRES as fg ON f.id = fg.film_id " +
                "left join GENRES as g ON fg.GENRE_ID = g.GENRE_ID " +
                "left join likes as l ON f.id = l.film_id " +
                "group by ID, FILM_NAME, DESCRIPTION,RELEASE_DATE, DURATION, f.MPA_ID " +
                "order by likes DESC " +
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
                .mpa(getMpaByFilmId(rs.getInt("id")))
                .genres(getGenreByFilmId(rs.getInt("id")))
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
                "where MPA_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, FilmDbStorage::mapRowToMpa, id);
    }

    public Mpa getMpaByFilmId(int id) {
        String sqlQuery = "select m.MPA_ID, MPA_NAME " +
                "from MPA AS m " +
                "join Films AS f ON m.MPA_ID = f.MPA_ID " +
                "where f.ID = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToMpa, id);
        if (mpas.size() != 1) {
            return null;
        }
        return mpas.get(0);
    }

    public List<Mpa> getAllMpa() {
        String sqlQuery = "select * from MPA";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToMpa);
    }

    public List<Genre> getGenreByFilmId(int id) {
        String sqlQuery = "select fg.genre_id, g.GENRE_NAME " +
                "from film_genres AS fg " +
                "join GENRES as g ON fg.GENRE_ID = g.GENRE_ID " +
                "where fg.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToGenre, id);
    }

    public Genre getGenreById(int id) {
        String sqlQuery = "select g.genre_id, g.GENRE_NAME " +
                "from GENRES as g " +
                "where GENRE_ID = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToGenre, id);
        if (genres.size() != 1) {
            return null;
        }
        return genres.get(0);
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "select * from GENRES";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToGenre);
    }
}
