package ru.yandex.practicum.filmorate.dao;

public interface FilmReviewStorage {
    void addLikeToReview(Integer reviewId, Long userId);

    void deleteLikeFromReview(Integer reviewId, Long userId);

    void addDislikeToReview(Integer reviewId, Long userId);

    void deleteDislikeFromReview(Integer reviewId, Long userId);
}