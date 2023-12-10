package ru.yandex.practicum.filmorate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

/**
 * Класс-контроллер для CRUD операций с отзывами и реализации API со свойством <b>ReviewDbService</b>.
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    /**
     * Поле сервис
     */
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Добавляет отзыв в хранилище.
     *
     * @param review объект отзыва.
     */
    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(
            @RequestParam(name = "filmId", defaultValue = "-1", required = false) Long filmId,
            @RequestParam(name = "count", defaultValue = "10", required = false) int count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeAReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeAReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeOfReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeOfReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteDislikeFromReview(id, userId);
    }
}