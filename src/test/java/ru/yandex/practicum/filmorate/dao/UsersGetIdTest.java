package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UsersGetIdTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void getUsersId() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user1 = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        userStorage.createUser(user1);
        assertThat(userStorage.getUsersId(user1.getId())).isNotNull().usingRecursiveComparison().isEqualTo(user1);
    }
}