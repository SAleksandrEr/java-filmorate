package ru.yandex.practicum.filmorate.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;


    @BeforeEach
    void start() {
        UserStorage userStorage = new InMemoryUserStorage();

        UserService userService = new UserService(userStorage);

        userController = new UserController(userStorage, userService);
    }

    @Test
    void validateUserNegative() {
        User user = User.builder()
                .name("")
                .login("Test")
                .email("test@test.ru")
                .birthday(LocalDate.of(1990,06,01))
                .friends(new HashSet<>())
                .build();
        userController.createUser(user);
        assertEquals(user.getName(),user.getLogin());
    }

    @Test
    void validateUserNull() {
        User user = User.builder()
                .name(null)
                .login("Test")
                .email("test@test.ru")
                .birthday(LocalDate.of(1990,06,01))
                .friends(new HashSet<>())
                .build();
        userController.createUser(user);
        assertEquals(user.getName(),user.getLogin());
    }
}