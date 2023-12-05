package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilm();

    Film getFilmsId(Long id);

    void filmDeleteById(Long filmId);

    List<Film> getFilmsByDirectorId(Long id, String sortBy);

    List<Film> searchNameFilmsAndDirectors(String query, List<String> by);

    List<Film> getCommonFilms(Long userId);

    List<Film> getPopularFilms(Long count, Long genreId, Long year);
}
