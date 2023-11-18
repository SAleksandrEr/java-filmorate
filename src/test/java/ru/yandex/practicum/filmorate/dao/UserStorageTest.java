package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void createUser() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        assertThat(userStorage.createUser(user)).isNotNull().usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void updateUser() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);

        User user = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        userStorage.createUser(user);
        user = User.builder().email("emailUser2").login("loginUser2")
                .name("nameUser2").birthday(LocalDate.of(2005,10,21)).id(1L).build();
        assertThat(userStorage.updateUser(user)).isNotNull().usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void getAllUser() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user1 = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        userStorage.createUser(user1);
        User user2 = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(2L).build();
        userStorage.createUser(user2);
        User user3 = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(3L).build();
        userStorage.createUser(user3);
        List<User> listUser = new ArrayList<>();
        listUser.add(user1);
        listUser.add(user2);
        listUser.add(user3);
        assertThat(userStorage.getAllUser()).isNotNull().usingRecursiveComparison().isEqualTo(listUser);
    }

    @Test
    void getUsersId() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user1 = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        userStorage.createUser(user1);
        assertThat(userStorage.getUsersId(user1.getId())).isNotNull().usingRecursiveComparison().isEqualTo(user1);
    }
}
