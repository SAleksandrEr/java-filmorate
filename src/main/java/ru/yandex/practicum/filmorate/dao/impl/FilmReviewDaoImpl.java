package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmReviewStorage;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class FilmReviewDaoImpl implements FilmReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeToReview(Long reviewId, Long userId) {
        jdbcTemplate.update("MERGE INTO film_reviews (review_id, user_id, is_like) VALUES (?, ?, ?)",
                reviewId, userId, Boolean.TRUE);
        updateUsefulnessScore(reviewId);
    }

    @Override
    public void deleteLikeFromReview(Long reviewId, Long userId) {
        jdbcTemplate.update("DELETE FROM film_reviews WHERE review_id=? AND user_id=?");
        updateUsefulnessScore(reviewId);
    }

    @Override
    public void addDislikeToReview(Long reviewId, Long userId) {
        jdbcTemplate.update("MERGE INTO film_reviews (review_id, user_id, is_like) VALUES (?, ?, ?)",
                reviewId, userId, Boolean.FALSE);
        updateUsefulnessScore(reviewId);
    }

    @Override
    public void deleteDislikeFromReview(Long reviewId, Long userId) {
        jdbcTemplate.update("DELETE FROM film_reviews WHERE review_id=? AND user_id=?");
        updateUsefulnessScore(reviewId);
    }

    private void updateUsefulnessScore(Long reviewId) {
        int usefulnessScore = getUsefulnessScore(reviewId);
        jdbcTemplate.update("UPDATE reviews SET useful=? WHERE review_id=?", usefulnessScore, reviewId);
    }

    private int getUsefulnessScore(Long reviewId) {
        return jdbcTemplate.query("SELECT SUM(CASE WHEN is_like = TRUE THEN 1 ELSE -1 END) useful " +
                        "FROM film_reviews WHERE review_id =?", this::makeFilmReview, reviewId)
                .stream().findAny().orElse(0);
    }

    public Integer makeFilmReview(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("useful");
    }
}