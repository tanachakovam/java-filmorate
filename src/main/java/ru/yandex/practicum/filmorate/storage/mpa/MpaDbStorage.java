package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "select m.MPA_ID, MPA_NAME " +
                "from MPA AS m " +
                "where MPA_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, MpaDbStorage::mapRowToMpa, id);
    }

    @Override
    public Mpa getMpaByFilmId(int id) {
        String sqlQuery = "select m.MPA_ID, MPA_NAME " +
                "from MPA AS m " +
                "join Films AS f ON m.MPA_ID = f.MPA_ID " +
                "where f.ID = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, MpaDbStorage::mapRowToMpa, id);
        if (mpas.size() != 1) {
            return null;
        }
        return mpas.get(0);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "select * from MPA";
        return jdbcTemplate.query(sqlQuery, MpaDbStorage::mapRowToMpa);
    }

    private static Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("MPA.mpa_name"))
                .build();
    }
}