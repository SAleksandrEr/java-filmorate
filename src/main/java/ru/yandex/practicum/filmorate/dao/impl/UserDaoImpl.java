package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component("userDaoImpl")
public class UserDaoImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO public.User_filmorate (email_user, login_user, name_user, birthday_user)" +
                " VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            return getUsersId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        } else {
            throw new DataNotFoundException("The User has not been add " + user);
        }
    }

    @Override
    public User updateUser(User user) {
        getUsersId(user.getId());
        String sqlQuery = "UPDATE User_filmorate SET email_user = ?, login_user = ?, name_user = ?, birthday_user = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                            Date.valueOf(user.getBirthday()), user.getId());
        return user;
    }

    @Override
    public List<User> getAllUser() {
        String sql = "SELECT * FROM User_filmorate";
        List<User> users = jdbcTemplate.query(sql, this::makeUser);
        return users;
    }

    @Override
    public User getUsersId(Long id) {
        String sql = "SELECT * FROM User_filmorate WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, this::makeUser,id);
        if (users.size() != 1) {
            throw new DataNotFoundException("Data not found " + id + " - " + users);
        }
        return users.get(0);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("user_id");
        String emailUser = rs.getString("email_user");
        String loginUser = rs.getString("login_user");
        String nameUser = rs.getString("name_user");
        LocalDate birthdayUser = rs.getDate("birthday_user").toLocalDate();
        return User.builder().email(emailUser).login(loginUser)
                .name(nameUser).birthday(birthdayUser).id(id).build();
    }

    @Override
    public void deleteUserById(Long id) {
        try {
            jdbcTemplate.update("DELETE FROM USER_FILMORATE WHERE USER_ID=?", id);
            jdbcTemplate.update("DELETE FROM Friends WHERE friends_id=? or user_id=?", id, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("не верный id пользователя ");
        }
    }
}