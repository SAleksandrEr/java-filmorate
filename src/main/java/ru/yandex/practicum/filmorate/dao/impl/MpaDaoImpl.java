package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDaoImpl implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM Mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa findMpaId(Long id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM Mpa WHERE mpa_id = ?", id);
        if (mpaRows.next()) {
            String nameMpa = mpaRows.getString("name_mpa");
            long mpaId = mpaRows.getInt("mpa_id");
            return Mpa.builder().name(nameMpa).id(mpaId).build();
        } else {
            throw new DataNotFoundException("Mpa not found " + id);
        }
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        long id = rs.getLong("mpa_id");
        String nameMpa = rs.getString("name_mpa");
        return Mpa.builder().name(nameMpa).id(id).build();
    }
}
