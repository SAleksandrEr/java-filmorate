package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDaoImpl implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genres> getAllGenres() {
        String sql = "SELECT * FROM Genre_list";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenresList(rs));
    }

    @Override
    public Genres findGenresId(Long id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM Genre_list WHERE generelist_id = ?", id);
        if (genreRows.next()) {
            Long genreId = genreRows.getLong("generelist_id");
            String nameGenre = genreRows.getString("description_genre");
            return Genres.builder().name(nameGenre).id(genreId).build();
        } else {
            throw new DataNotFoundException("Жанры не найдены по id " + id);
        }
    }

    @Override
    public List<Genres> getFilmGenres(Long idFilm) {
        String sql = "SELECT DISTINCT genre_id, gl.DESCRIPTION_GENRE FROM Genre AS g INNER JOIN Genre_list AS gl " +
                "ON g.GENRE_ID = gl.generelist_id WHERE film_id =?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenres(rs), idFilm);
    }

    @Override
    public List<Genres> createGenresFilm(List<Genres> genres, Long idFilm) {
        for (Genres listGenres : genres) {
            String sqlQuery = "INSERT INTO public.Genre (genre_id, film_id) VALUES (?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                stmt.setLong(1, listGenres.getId());
                stmt.setLong(2, idFilm);
                return stmt;
            }, keyHolder);
            if (keyHolder.getKey() == null) {
                throw new DataNotFoundException("Жанры фильмов не были добавлены с id " +
                        idFilm + " - " + genres);
            }
        }
        return getFilmGenres(idFilm);
    }

    @Override
    public List<Genres> updateGenresFilm(List<Genres> genres, Long idFilm) {
        if (genres.isEmpty() || !getFilmGenres(idFilm).isEmpty()) {
            deleteGenresFilm(idFilm);
        }
        if (!genres.isEmpty()) {
                createGenresFilm(genres, idFilm);
        }
        return getFilmGenres(idFilm);
    }

    private boolean deleteGenresFilm(Long idFilm) {
            String sqlQuery = "DELETE FROM Genre WHERE film_id = ?";
            return jdbcTemplate.update(sqlQuery, idFilm) > 0;
    }

    private Genres makeGenres(ResultSet rs) throws SQLException {
        long id = rs.getInt("genre_id");
        String descriptionGenre = rs.getString("description_genre");
        return Genres.builder().name(descriptionGenre).id(id).build();
    }

    private Genres makeGenresList(ResultSet rs) throws SQLException {
        Long id = rs.getLong("generelist_id");
        String descriptionGenre = rs.getString("description_genre");
        return Genres.builder().name(descriptionGenre).id(id).build();
    }
}
