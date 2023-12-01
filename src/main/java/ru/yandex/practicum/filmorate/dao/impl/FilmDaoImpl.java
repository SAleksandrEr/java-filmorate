package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
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
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            return getFilmsId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            throw new DataNotFoundException("The film has not been created " + film);
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
        String sql = "SELECT DISTINCT * FROM FILM AS f " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN Genre AS g ON f.unit_id = g.film_id " +
                "LEFT JOIN Genre_list AS gl ON g.genre_id = gl.generelist_id";
        return jdbcTemplate.query(sql, new ResultSetExtractor<List<Film>>() {
                    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Film> listFilm = new ArrayList<>();
                        Map<Long, Film> mapFilm = new HashMap<>();
                        Genres genre;
                        Film film;
                        Film prevFilm = new Film();
                        prevFilm.setId(0L);
                        List<Genres> genresList = new ArrayList<>();
                        while (rs.next()) {
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
                                if (id == prevFilm.getId()) {
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
        );
    }

    @Override
    public Film getFilmsId(Long id) {
        String sql = "SELECT DISTINCT * FROM FILM AS f " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN Genre AS g ON f.unit_id = g.film_id " +
                "LEFT JOIN Genre_list AS gl ON g.genre_id = gl.generelist_id " +
                "WHERE f.unit_id = ?";
        List<Film> films = jdbcTemplate.query(sql, new ResultSetExtractor<List<Film>>() {
                    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Film> listFilm = new ArrayList<>();
                        List<Genres> genresList = new ArrayList<>();
                        Film film;
                        Genres genre;
                        Map<Long, Film> mapFilm = new HashMap<>();
                        while (rs.next()) {
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
                                genresList.add(genre);
                            }
                            film = Film.builder().name(nameFilm).description(description)
                                    .releaseDate(releaseDateFilm).duration(durationFilm)
                                    .mpa(Mpa.builder().name(nameMpa).id(mpaId).build()).genres(genresList).id(id).build();

                            mapFilm.put(id, film);
                        }
                        listFilm.addAll(mapFilm.values());
                        return listFilm;
                    }
                }, id);
        if (Objects.requireNonNull(films).size() != 1) {
            throw new DataNotFoundException("Data not found " + id + films);
        }
        return films.get(0);
    }

    @Override
    public void filmDeleteById(Long id) {
        String sqlQuery = "DELETE FROM Film WHERE unit_id=?";
        try {
            jdbcTemplate.update(sqlQuery, id);
        } catch (RuntimeException e) {
            throw new DataNotFoundException("Фильм не найден");
        }
    }

    @Override
   public List<Film> searchNameFilmsAndDirectors(String query, List<String> by) {
        String queryTitle = "%_%";
        String queryDirector = "%_%";
        if (!(query == null)) {
            for (String s : by) {
                if (Objects.equals(s, "title")) {
                    queryTitle = "%" + query.toLowerCase() + "%";
                }
                if (Objects.equals(s, "director")) {
                    queryDirector = "%" + query.toLowerCase() + "%";
                }
            }
        }
        String sql = "SELECT * FROM (SELECT l.film_id, COUNT(l.user_id) AS noun " +
                "FROM Likes AS l " +
                "GROUP BY l.film_id) AS film_lik " +
                "RIGHT JOIN Film AS f ON f.unit_id = film_lik.film_id " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN Genre AS g ON f.unit_id = g.film_id " +
                "LEFT JOIN Genre_list AS gl ON g.genre_id = gl.generelist_id " +
                "WHERE (LOWER(f.name_film) LIKE ? " +
                "OR LOWER(gl.DESCRIPTION_GENRE) LIKE ?) " +
                "ORDER BY noun DESC";
        return jdbcTemplate.query(sql, new ResultSetExtractor<List<Film>>() {
                    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Film> listFilm = new ArrayList<>();
                        Genres genre;
                        Film film;
                        Film prevFilm = new Film();
                        prevFilm.setId(0L);
                        List<Genres> genresList = new ArrayList<>();
                        while (rs.next()) {
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
                                if (id == prevFilm.getId()) {
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
                            listFilm.add(film);
                        }
                        return listFilm;
                    }
                }, queryTitle, queryDirector);
    }
}