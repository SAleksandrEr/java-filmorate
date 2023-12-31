package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RecommendationStorage {

    List<Film> getRecommendedFilms(Long id);
}