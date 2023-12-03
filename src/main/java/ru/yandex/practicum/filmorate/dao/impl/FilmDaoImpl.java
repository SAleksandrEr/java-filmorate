package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Directors;
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

    private final GenreStorage genreStorage;

    private final DirectorStorage directorStorage;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO Film (name_film, description_film, " +
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
        genreStorage.createGenresFilm(film.getGenres(), film.getId());
        directorStorage.createDirectorsFilm(film.getDirectors(), film.getId());
        return getFilmsId(film.getId());
    }

    @Override
    public List<Film> getAllFilm() {
        String sql = "SELECT DISTINCT * FROM FILM AS f " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN Genre AS g ON f.unit_id = g.film_id " +
                "LEFT JOIN Genre_list AS gl ON g.genre_id = gl.generelist_id " +
                "LEFT JOIN Film_director AS fd ON f.unit_id = fd.film_id " +
                "LEFT JOIN Directors AS d ON fd.director_id = d.director_id";
        return jdbcTemplate.query(sql, this::createsFilm);
    }

    @Override
    public Film getFilmsId(Long id) {
        String sql = "SELECT DISTINCT * FROM FILM AS f " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN Genre AS g ON f.unit_id = g.film_id " +
                "LEFT JOIN Genre_list AS gl ON g.genre_id = gl.generelist_id " +
                "LEFT JOIN Film_director AS fd ON f.unit_id = fd.film_id " +
                "LEFT JOIN Directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.unit_id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::createsFilm, id);
        if (films.size() != 1) {
            throw new DataNotFoundException(String.format("Фильм c id %s отсутствует", id));
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
    public List<Film> getFilmsByDirectorId(Long id, String sortBy) {
        List<Film> films;
        if (sortBy.equals("likes")) {
            films = getSortedFilmsByLikes(id);
        } else if (sortBy.equals("year")) {
            films = getSortedFilmsByYear(id);
        } else {
            throw new DataNotFoundException("Нет возможности отсортировать по " + sortBy);
        }
        return films;
    }

    private List<Film> getSortedFilmsByYear(Long id) {
        String sql = "SELECT * " +
                "FROM Film AS f " +
                "LEFT JOIN Genre AS g ON f.unit_id = g.film_id " +
                "LEFT JOIN Genre_list AS gl ON g.genre_id = gl.generelist_id " +
                "LEFT JOIN Film_director AS fd ON f.unit_id = fd.film_id " +
                "LEFT JOIN Directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE d.director_id = ? " +
                "GROUP BY d.director_id, f.releaseDate_film " +
                "ORDER BY EXTRACT(YEAR FROM CAST(f.releaseDate_film AS DATE))";
        return jdbcTemplate.query(sql, this::createsFilm, id);
    }

    private List<Film> getSortedFilmsByLikes(Long id) {
        String sql = "SELECT * FROM Film AS f " +
                "LEFT JOIN Film_director AS fd ON f.unit_id = fd.film_id " +
                "LEFT JOIN Genre AS g ON f.unit_id = g.film_id " +
                "LEFT JOIN Genre_list AS gl ON g.genre_id = gl.generelist_id " +
                "LEFT JOIN Directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN Likes AS l ON l.film_id = f.unit_id " +
                "LEFT JOIN Mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE d.director_id = ? " +
                "GROUP BY d.director_id, gl.generelist_id " +
                "ORDER BY COUNT(l.film_id) DESC";
        return jdbcTemplate.query(sql, this::createsFilm, id);
    }

    private List<Film> createsFilm(ResultSet rs) throws SQLException {
        ResultSetExtractor<List<Film>> resultSetExtractor = rs1 -> {
            Map<Long, Film> list = new LinkedHashMap<>();
            while (rs1.next()) {
                if (list.containsKey(rs1.getLong("unit_id"))) {
                    list.get(rs1.getLong("unit_id")).getGenres().add(Genres.builder()
                            .id(rs1.getLong("genre_id"))
                            .name(rs1.getString("description_genre"))
                            .build());
                    if (rs1.getLong("director_id") != 0) {
                        list.get(rs1.getLong("unit_id")).getDirectors().add(Directors.builder()
                                .id(rs1.getLong("director_id"))
                                .name(rs1.getString("name_director"))
                                .build());
                    }
                } else {
                    Film film = Film.builder()
                            .id(rs1.getLong("unit_id"))
                            .name(rs1.getString("name_film"))
                            .description(rs1.getString("description_film"))
                            .releaseDate(rs1.getDate("releaseDate_film").toLocalDate())
                            .duration(rs1.getInt("duration_film"))
                            .mpa(Mpa.builder()
                                    .id(rs1.getLong("mpa_id"))
                                    .name(rs1.getString("name_mpa"))
                                    .build())
                            .genres(new ArrayList<>())
                            .directors(new ArrayList<>())
                            .build();

                    if (rs1.getLong("genre_id") != 0) {
                        film.getGenres().add(Genres.builder()
                                .id(rs1.getLong("genre_id"))
                                .name(rs1.getString("description_genre"))
                                .build());
                    }

                    if (rs1.getLong("director_id") != 0) {
                        film.getDirectors().add(Directors.builder()
                                .id(rs1.getLong("director_id"))
                                .name(rs1.getString("name_director"))
                                .build());
                    }

                    list.put(film.getId(), film);
                }
            }
            return new ArrayList<>(list.values());
        };
        return resultSetExtractor.extractData(rs);
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