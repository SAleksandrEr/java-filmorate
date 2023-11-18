package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

public interface GenreStorage {

    List<Genres> getAllGenres();

    Genres findGenresId(Long id);

    List<Genres> getFilmGenres(Long id);

    List<Genres> createGenresFilm(List<Genres> genres, Long idFilm);

    List<Genres> updateGenresFilm(List<Genres> genres, Long idFilm);
 }
