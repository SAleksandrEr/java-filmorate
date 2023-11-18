package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("filmDaoImpl")
public class FilmDaoImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO public.Film (name_film, description_film, " +
                "releaseDate_film, duration_film, mpa_id) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"unit_id"});
                stmt.setString(1,film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setLong(5,film.getMpa().getId());
                return stmt;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                return getFilmsId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            } else {
                throw new DataNotFoundException("FilmID");
            }
        }

    @Override
    public Film updateFilm(Film film) {
           getFilmsId(film.getId());
           String sqlQuery = "UPDATE Film SET NAME_FILM = ?, DESCRIPTION_FILM = ?, " +
                   "RELEASEDATE_FILM = ?, DURATION_FILM = ?, MPA_ID = ? WHERE UNIT_ID = ?";
           jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                               film.getDuration(), film.getMpa().getId(), film.getId());
        return getFilmsId(film.getId());
    }

    @Override
    public List<Film> getAllFilm() {
        String sql = "SELECT * FROM FILM AS f LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film getFilmsId(Long id) {
        String sql = "SELECT * FROM FILM AS f LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id WHERE f.unit_id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm,id);
        if (films.size() != 1) {
            throw new DataNotFoundException("Data not found " + id + films);
        }
        return films.get(0);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("unit_id");
        String nameFilm = rs.getString("name_film");
        String description = rs.getString("description_film");
        Integer durationFilm = rs.getInt("duration_film");
        LocalDate releaseDateFilm = rs.getDate("releaseDate_film").toLocalDate();
        long mpaId = rs.getLong("mpa_id");
        String nameMpa = rs.getString("name_mpa");
        return Film.builder().name(nameFilm).description(description)
                .releaseDate(releaseDateFilm).duration(durationFilm)
                .mpa(Mpa.builder().name(nameMpa).id(mpaId).build()).genres(new ArrayList<>()).id(id).build();
    }
}
