package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmReviewStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    private final UserService userService;

    private final FilmService filmService;

    private final FilmReviewStorage filmReview;

    public Review addReview(Review review) {
        checker(review.getFilmId(), review.getUserId());
        validationReview(review);
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        checker(review.getFilmId(), review.getUserId());
        validationReview(review);
        return reviewStorage.updateReview(review);
    }

    public void deleteReviewById(Integer id) {
        if (!reviewStorage.isContains(id) || id == null) {
            throw new DataNotFoundException("Отзыв не найден: пустой или неправильный идентификатор");
        }
        reviewStorage.deleteReviewById(id);
    }

    public Review getReviewById(Integer id) {
        if (!reviewStorage.isContains(id) || id == null) {
            throw new DataNotFoundException("Отзыв не найден: пустой или неправильный идентификатор");
        }
        return reviewStorage.getReviewById(id);
    }

    public List<Review> getReviews(Long id, int count) {
        return reviewStorage.getReviews(id, count);
    }

    public void addLikeToReview(Integer reviewId, Long userId) {
        filmReview.addLikeToReview(reviewId, userId);
    }

    public void addDislikeToReview(Integer reviewId, Long userId) {
        filmReview.addDislikeToReview(reviewId, userId);
    }

    public void deleteLikeFromReview(Integer reviewId, Long userId) {
        filmReview.deleteLikeFromReview(reviewId, userId);
    }

    public void deleteDislikeFromReview(Integer reviewId, Long userId) {
        filmReview.deleteDislikeFromReview(reviewId, userId);
    }

    private void checker(Long filmId, Long userId) {
        if (filmId == null || filmService.findFilmsId(filmId) == null) {
            throw new DataNotFoundException("Не найден фильм c идентификатором " + filmId);
        }
        if (userId == null || userService.findUsersId(userId) == null) {
            throw new DataNotFoundException("Не найден пользователь с идентификатором " + userId);
        }
    }

    public static void validationReview(Review review) {
        log.debug("validationReview({})", review);
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Поле с описанием отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Попытка присвоить значению поля isPositive null");
        }
        review.setUseful(0);
    }
}