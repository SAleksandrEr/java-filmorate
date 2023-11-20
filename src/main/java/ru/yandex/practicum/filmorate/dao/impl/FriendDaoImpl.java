package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class FriendDaoImpl implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendDaoImpl(JdbcTemplate jdbcTemplate, UserDaoImpl userDaoImpl) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findFriendUserId(Long userId) {
       String sql = "SELECT * FROM (SELECT f.friends_id FROM Friends AS f WHERE f.user_id = ?) AS userfr " +
               "INNER JOIN User_filmorate AS u ON u.user_id = userfr.friends_id ORDER BY u.user_id";
       return jdbcTemplate.query(sql, this::makeFriend,userId);
    }

    @Override
    public List<Friend> createFriendUser(Long friendId, Long userId) {
        String sqlQuery = "INSERT INTO Friends (friends_id, user_id) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setLong(1, friendId);
            stmt.setLong(2, userId);
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            return getFriendUserId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            throw new DataNotFoundException("The Friend has not been add with id " + friendId + " - " + userId);
        }
    }

    @Override
    public List<Friend> getFriendUserId(Long userId) {
        String sql = "SELECT * FROM Friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::makeFriendList,userId);
    }

    @Override
    public List<User> findUsersOtherId(Long id, Long otherId) {
        String sql = "SELECT * FROM (SELECT f.friends_id, COUNT(f.friends_id) AS noun " +
                "FROM Friends AS f WHERE f.user_id IN (?, ?) GROUP BY f.friends_id HAVING noun > 1) AS userfr " +
                "INNER JOIN User_filmorate AS u ON u.user_id = userfr.friends_id GROUP BY u.user_id ORDER BY u.user_id";
        return jdbcTemplate.query(sql, this::makeFriend, id, otherId);
    }

    @Override
    public boolean deleteFriendsId(Long userId, Long friendId) {
            String sqlQuery = "DELETE FROM Friends WHERE user_id = ? AND friends_id = ?";
            return jdbcTemplate.update(sqlQuery, userId, friendId) > 0;
        }

    private Friend makeFriendList(ResultSet rs, int rowNum) throws SQLException {
        long userId = rs.getLong("user_id");
        Long friendsId = rs.getLong("friends_id");
        boolean friendshipStatus = rs.getBoolean("friendship_status");
        return Friend.builder().friendsId(friendsId).userId(userId)
                .friendshipStatus(friendshipStatus).build();
    }

    private User makeFriend(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("user_id");
        String emailUser = rs.getString("email_user");
        String loginUser = rs.getString("login_user");
        String nameUser = rs.getString("name_user");
        LocalDate birthdayUser = rs.getDate("birthday_user").toLocalDate();
        return User.builder().email(emailUser).login(loginUser)
                .name(nameUser).birthday(birthdayUser).id(id).build();
    }
}
