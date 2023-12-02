package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Directors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Component
public class DirectorDaoImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Directors> getAllDirectors() {
        String sql = "SELECT * FROM Directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirectorList(rs));
    }

    private Directors makeDirectorList(ResultSet rs) throws SQLException {
        Long id = rs.getLong("director_id");
        String directorName = rs.getString("name_director");
        return Directors.builder().name(directorName).id(id).build();
    }

    @Override
    public Directors getDirectorByID(Long id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM Directors WHERE director_id = ?", id);
        if (directorRows.next()) {
            Long directorId = directorRows.getLong("director_id");
            String nameDirector = directorRows.getString("name_director");
            return Directors.builder().name(nameDirector).id(directorId).build();
        } else {
            throw new DataNotFoundException("The Directors has not been find with id " + id);
        }
    }

    @Override
    public Directors updateDirector(Directors director) {
        String sql = "UPDATE Directors SET name_director = ? WHERE director_id = ?;";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return getDirectorByID(director.getId());
    }

    @Override
    public Directors createDirector(Directors director) {
        String sql = "INSERT INTO Directors(name_director) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        if (keyHolder.getKey() == null) {
            throw new DataNotFoundException("The Director has not been add with id " +
                    director.getId());
        }
        return director;
    }

    @Override
    public void deleteDirector(Long id) {
        String sql = "DELETE FROM Directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Directors> createDirectorsFilm(List<Directors> directors, Long idFilm) {
        for (Directors listDirectors : directors) {
            String sqlQuery = "INSERT INTO public.Film_director (director_id, film_id) VALUES (?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                stmt.setLong(1, listDirectors.getId());
                stmt.setLong(2, idFilm);
                return stmt;
            }, keyHolder);
            if (keyHolder.getKey() == null) {
                throw new DataNotFoundException("The Directors for Film has not been add with id " +
                        idFilm + " - " + directors);
            }
        }
        return getFilmDirectors(idFilm);
    }

    @Override
    public List<Directors> getFilmDirectors(Long idFilm) {
        String sql = "SELECT DISTINCT fd.director_id, d.name_director FROM Film_director AS fd INNER JOIN Directors AS d " +
                "ON fd.director_id = d.director_id WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirectors(rs), idFilm);
    }

    private Directors makeDirectors(ResultSet rs) throws SQLException {
        long id = rs.getInt("director_id");
        String descriptionGenre = rs.getString("name_director");
        return Directors.builder().name(descriptionGenre).id(id).build();
    }

    @Override
    public List<Directors> updateDirectorsFilm(List<Directors> directors, Long idFilm) {
        if (directors.isEmpty() || !getFilmDirectors(idFilm).isEmpty()) {
            deleteDirectorsFilm(idFilm);
        }
        if (!directors.isEmpty()) {
            createDirectorsFilm(directors, idFilm);
        }
        return getFilmDirectors(idFilm);
    }

    private boolean deleteDirectorsFilm(Long idFilm) {
        String sqlQuery = "DELETE FROM Film_director WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, idFilm) > 0;
    }

}