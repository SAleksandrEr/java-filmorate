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
class UserDaoTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void createUser() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        assertThat(userStorage.createUser(user)).isNotNull().usingRecursiveComparison()
                .comparingOnlyFields(String.valueOf(user.getId())).isEqualTo(user);
    }

    @Test
    void getUsersId() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        User newUuser = userStorage.createUser(user);
        assertThat(userStorage.getUsersId(newUuser.getId())).isNotNull().usingRecursiveComparison()
                .comparingOnlyFields(String.valueOf(user.getId())).isEqualTo(user);
    }

    @Test
    void updateUser() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user = User.builder().email("emailUser").login("loginUser")
                .name("nameUser").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        user = userStorage.createUser(user);
        User newUser = User.builder().email("emailUser2").login("loginUser2")
                .name("nameUser2").birthday(LocalDate.of(2005,10,21))
                .id(1L).id(user.getId()).build();
        assertThat(userStorage.updateUser(newUser)).isNotNull().usingRecursiveComparison()
                .comparingOnlyFields(String.valueOf(user.getId())).isEqualTo(user);
    }

    @Test
    void getAllUser() {
        UserStorage userStorage = new UserDaoImpl(jdbcTemplate);
        User user1 = User.builder().email("emailUser1").login("loginUser")
                .name("nameUser2").birthday(LocalDate.of(2003,9,21)).id(1L).build();
        userStorage.createUser(user1);
        User user2 = User.builder().email("emailUser").login("loginUser")
                .name("nameUser3").birthday(LocalDate.of(2003,9,21)).id(2L).build();
        userStorage.createUser(user2);
        User user3 = User.builder().email("emailUser").login("loginUser")
                .name("nameUser4").birthday(LocalDate.of(2003,9,21)).id(3L).build();
        userStorage.createUser(user3);
        List<User> listUser = new ArrayList<>();
        listUser.add(user1);
        listUser.add(user2);
        listUser.add(user3);
        assertThat(userStorage.getAllUser()).isNotNull().usingRecursiveComparison().comparingOnlyFields(String.valueOf(user3.getId()))
                .isEqualTo(listUser);
    }
}