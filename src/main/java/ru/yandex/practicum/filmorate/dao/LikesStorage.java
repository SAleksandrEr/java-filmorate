package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {

    boolean deleteLikeId(Long id, Long userId);

    Long createLikeFilm(Long id, Long userId);

    List<Long> getPopularFilms(Long count, Long genreId, Long year);
}
