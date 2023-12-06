package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventsStorage;
import ru.yandex.practicum.filmorate.dao.FilmReviewStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    private final UserService userService;

    private final FilmService filmService;

    private final FilmReviewStorage filmReview;

    private final EventsStorage eventsStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, UserService userService, FilmService filmService,
                         FilmReviewStorage filmReview, EventsStorage eventsStorage) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.filmReview = filmReview;
        this.eventsStorage = eventsStorage;
    }

    public Review addReview(Review review) {
        filmService.findFilmsId(review.getFilmId());
        userService.findUsersId(review.getUserId());
        validationReview(review);
        review = reviewStorage.addReview(review);
        eventsStorage.createUserIdEvents(review.getReviewId(), review.getUserId(), EventType.REVIEW, EventOperation.ADD);
        log.info("Добавлен отзыв: {}", review);
        return review;
    }

    public Review updateReview(Review review) {
        filmService.findFilmsId(review.getFilmId());
        userService.findUsersId(review.getUserId());
        validationReview(review);
        review = reviewStorage.updateReview(review);
        eventsStorage.createUserIdEvents(review.getReviewId(), review.getUserId(), EventType.REVIEW, EventOperation.UPDATE);
        log.info("Обновлён отзыв: {}", review);
        return review;
    }

    public void deleteReviewById(Long id) {
        if (!reviewStorage.isContains(id) || id == null) {
            throw new DataNotFoundException("Отзыв не найден: пустой или неправильный идентификатор");
        }
        eventsStorage.createUserIdEvents(id, getReviewById(id).getUserId(), EventType.REVIEW, EventOperation.REMOVE);
        reviewStorage.deleteReviewById(id);
        log.info("Удалён отзыв с идентификатором {}", id);
    }

    public Review getReviewById(Long id) {
        if (!reviewStorage.isContains(id) || id == null) {
            throw new DataNotFoundException("Отзыв не найден: пустой или неправильный идентификатор");
        }
        return reviewStorage.getReviewById(id);
    }

    public List<Review> getReviews(Long id, int count) {
        log.info("Получен отзывы для фильма с идентификатором {}", id);
        return reviewStorage.getReviews(id, count);
    }

    public void addLikeToReview(Long reviewId, Long userId) {
        log.info("Добавлен лак на отзыв от пользователя {}", reviewId + " " + userId);
        filmReview.addLikeToReview(reviewId, userId);
    }

    public void addDislikeToReview(Long reviewId, Long userId) {
        log.info("Добавлен дизлайк на отзыв от пользователя {}", reviewId + " " + userId);
        filmReview.addDislikeToReview(reviewId, userId);
    }

    public void deleteLikeFromReview(Long reviewId, Long userId) {
        log.info("Удален лайк на отзыв от пользователя {}", reviewId + " " + userId);
        filmReview.deleteLikeFromReview(reviewId, userId);
    }

    public void deleteDislikeFromReview(Long reviewId, Long userId) {
        log.info("Удален дизлайк на отзыв от пользователя {}", reviewId + " " + userId);
        filmReview.deleteDislikeFromReview(reviewId, userId);
    }

    private static void validationReview(Review review) {
        log.info("validationReview({})", review);
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Поле с описанием отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Попытка присвоить значению поля isPositive null");
        }
    }
}