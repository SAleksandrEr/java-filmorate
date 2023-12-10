package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class ReviewDaoImpl implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        jdbcTemplate.update(
                "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?,?,?,?,?)",
                review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getUseful());
        return jdbcTemplate.queryForObject(
                "SELECT review_id, content, is_positive, user_id, film_id, useful " +
                        "FROM reviews WHERE content=? AND is_positive=? AND user_id=? AND film_id=? AND useful=?",
                this::makeReview, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(),
                review.getUseful());
    }

    @Override
    public Review updateReview(Review review) {
        jdbcTemplate.update("UPDATE reviews SET is_positive=?, content=? WHERE review_id=?",
                review.getIsPositive(), review.getContent(), review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReviewById(Long id) {
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id=?", id);
    }

    @Override
    public Review getReviewById(Long id) {
        String sql = "SELECT review_id, content, is_positive, user_id, film_id, useful " +
                        "FROM reviews WHERE review_id=?";
        List<Review> review = jdbcTemplate.query(sql, this::makeReview, id);
        if (review.size() != 1) {
            throw new DataNotFoundException("Data not found " + id + " - " + review);
        }
        log.info("Возвращён отзыв с идентификатором {}: {}", id, review);
        return review.get(0);
    }

    @Override
    public List<Review> getReviews(Long id, int count) {
        List<Review> list;
        if (id == -1) {
            list = new ArrayList<>(jdbcTemplate.query(
                    "SELECT review_id, content, is_positive, user_id, film_id, useful FROM reviews " +
                            "GROUP BY review_id ORDER BY useful DESC LIMIT ?", this::makeReview, count));
            log.info("Возвращён список всех отзывов: {}", list);
        } else {
            list = new ArrayList<>(jdbcTemplate.query(
                    "SELECT review_id, content, is_positive, user_id, film_id, useful FROM reviews " +
                            "WHERE film_id=? GROUP BY review_id ORDER BY useful DESC LIMIT ?",
                    this::makeReview, id, count));
            log.info("Возвращён список отзывов по идентификатору фильма {}: {}", id, list);
        }
        return list;
    }

    @Override
    public boolean isContains(Long id) {
        try {
            getReviewById(id);
            log.info("Информация по отзыву с идентификатором {} найдена", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.info("Нет информации по отзыву с идентификатором {}", id);
            return false;
        }
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder().reviewId(rs.getLong("review_id")).content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive")).userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id")).useful(rs.getInt("useful")).build();
    }
}