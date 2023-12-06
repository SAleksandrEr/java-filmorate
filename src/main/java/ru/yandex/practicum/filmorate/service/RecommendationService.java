package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RecommendationStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@Service
public class RecommendationService {

    public RecommendationService(RecommendationStorage recommendationStorage) {
        this.recommendationStorage = recommendationStorage;
    }

    private final RecommendationStorage recommendationStorage;

    public List<Film> getFilmRecommendations(Long userId) {
        return recommendationStorage.getRecommendedFilms(userId);
    }
}