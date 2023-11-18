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
class CreateUserTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void createUser() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        assertThat(userStorage.createUser(user)).isNotNull().usingRecursiveComparison().isEqualTo(user);
    }
}
