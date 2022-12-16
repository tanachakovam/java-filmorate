package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private static Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    @Override
    public List<Genre> getGenreByFilmId(int id) {
        String sqlQuery = "select fg.genre_id, g.GENRE_NAME " +
                "from film_genres AS fg " +
                "join GENRES as g ON fg.GENRE_ID = g.GENRE_ID " +
                "where fg.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre, id);
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "select g.genre_id, g.GENRE_NAME " +
                "from GENRES as g " +
                "where GENRE_ID = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre, id);
        if (genres.size() != 1) {
            return null;
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "select * from GENRES";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre);
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
}
