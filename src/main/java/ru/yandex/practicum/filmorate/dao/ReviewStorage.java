package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dao.impl.ReviewDaoImpl;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

/**
 * Интерфейс для работы с логикой касающиеся отзывов реализован в {@link ReviewDaoImpl}.
 */
public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReviewById(Long id);

    Review getReviewById(Long id);

    List<Review> getReviews(Long id, int count);

    boolean isContains(Long id);

    void increaseScore(Review review);

    void decreaseScore(Review review);
}