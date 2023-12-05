package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
public class LikesDaoImpl implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    public LikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean deleteLikeId(Long filmId, Long userId) {
    String sqlQuery = "DELETE FROM Likes WHERE film_id = ? AND user_id = ?";
    if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
        return true;
        } else {
            throw new DataNotFoundException("The Likes has not been delete for filmId from userId "
                    + filmId + " - " + userId);
        }
    }

    @Override
    public Long createLikeFilm(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO Likes (user_id, film_id) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setLong(1, userId);
            stmt.setLong(2, filmId);
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            return Objects.requireNonNull(keyHolder.getKey()).longValue();
        } else {
            throw new DataNotFoundException("The Likes has not been add for filmId from userId "
                    + filmId + " - " + userId);
        }
    }

    @Override
    public List<Film> findFilmsOfLikes(Long count) {
        String sql = "SELECT * FROM (SELECT l.film_id, COUNT(l.user_id) " +
                "AS noun FROM Likes AS l GROUP BY l.film_id) " +
                "AS film_lik RIGHT JOIN Film AS f ON f.unit_id = film_lik.film_id " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "ORDER BY noun DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::makeFilm,count);
    }

    public Collection<Film> findFilmsOfLikesByUser(Long userId) {
        String sql = "SELECT * FROM film WHERE film_id IN (SELECT film_id FROM likes WHERE user_id = ?)";
        return jdbcTemplate.query(sql, this::makeFilm,userId);
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
                .mpa(Mpa.builder().name(nameMpa).id(mpaId).build()).id(id).genres(new ArrayList<>()).build();
    }
}
