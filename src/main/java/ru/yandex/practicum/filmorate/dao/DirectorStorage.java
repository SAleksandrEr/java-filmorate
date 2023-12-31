package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Directors;

import java.util.List;

public interface DirectorStorage {

    List<Directors> getAllDirectors();

    Directors getDirectorByID(Long id);

    void updateDirector(Directors director);

    Directors createDirector(Directors director);

    void deleteDirector(Long id);

    List<Directors> createDirectorsFilm(List<Directors> directors, Long idFilm);

    List<Directors> getFilmDirectors(Long idFilm);

    boolean deleteDirectorsFilm(Long idFilm);
}
