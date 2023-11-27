package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
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
    public List<Film> findFilmsOfLikes(Integer count) {
        String sql = "SELECT DISTINCT * FROM (SELECT l.film_id, COUNT(l.user_id) " +
                "AS noun FROM Likes AS l GROUP BY l.film_id) " +
                "AS film_lik RIGHT JOIN Film AS f ON f.unit_id = film_lik.film_id " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN Genre AS g ON f.unit_id = g.film_id " +
                "LEFT JOIN Genre_list AS gl ON g.genre_id = gl.generelist_id " +
                "ORDER BY noun DESC LIMIT ?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<List<Film>>() {
                    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Film> listFilm = new ArrayList<>();
                        Map<Long, Film> mapFilm = new HashMap<>();
                        Genres genre = new Genres();
                        Film film = new Film();
                        Film prevFilm = new Film();
                        prevFilm.setId(0L);
                        List<Genres> genresList = new ArrayList<>();
                        while (rs.next()) {
                            genre = null;
                            long id = rs.getLong("unit_id");
                            String nameFilm = rs.getString("name_film");
                            String description = rs.getString("description_film");
                            Integer durationFilm = rs.getInt("duration_film");
                            LocalDate releaseDateFilm = rs.getDate("releaseDate_film").toLocalDate();
                            long mpaId = rs.getLong("mpa_id");
                            String nameMpa = rs.getString("name_mpa");
                            long genreId = rs.getInt("genre_id");
                            String descriptionGenre = rs.getString("description_genre");
                            if (genreId != 0) {
                                genre = Genres.builder().name(descriptionGenre).id(genreId).build();
                                if (id == prevFilm.getId()){
                                    genresList.add(genre);
                                } else {
                                    genresList = new ArrayList<>();
                                    genresList.add(genre);
                                }
                                film = Film.builder().name(nameFilm).description(description)
                                        .releaseDate(releaseDateFilm).duration(durationFilm)
                                        .mpa(Mpa.builder().name(nameMpa).id(mpaId).build()).genres(genresList).id(id).build();
                            } else {
                                film = Film.builder().name(nameFilm).description(description)
                                        .releaseDate(releaseDateFilm).duration(durationFilm)
                                        .mpa(Mpa.builder().name(nameMpa).id(mpaId).build()).genres(new ArrayList<>()).id(id).build();
                            }
                            prevFilm = film;
                            mapFilm.put(id, film);
                        }
                        listFilm.addAll(mapFilm.values());
                        return listFilm;
                    }
                }
                , count);
    }
}
