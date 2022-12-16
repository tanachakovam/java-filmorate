package ru.yandex.practicum.filmorate.storage.film;


import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;


import ru.yandex.practicum.filmorate.exception.FilmReleaseException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;


import java.sql.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Primary
@Repository
public class FilmDbStorage implements FilmStorage {
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 18);
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;


    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID " +
                "from films AS f";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
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
        genreDbStorage.saveGenre(film);
        return film;
    }


    @Override
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
        genreDbStorage.saveGenre(film);
        return getFilm(film.getId());
    }

    @Override
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

    @Override
    public Film putLike(int id, int userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes");
        Map<String, Object> values = new HashMap<>();
        values.put("film_id", id);
        values.put("user_id", userId);
        simpleJdbcInsert.execute(values);
        return getFilm(id);
    }

    @Override
    public Film deleteLike(int id, int userId) {
        if (userId < 0) {
            throw new UserNotFoundException("User doesn't exist.");
        }
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return getFilm(id);
    }

    @Override
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
                .mpa(mpaDbStorage.getMpaByFilmId(rs.getInt("id")))
                .genres(genreDbStorage.getGenreByFilmId(rs.getInt("id")))
                .build();
    }
}
