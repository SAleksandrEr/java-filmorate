package ru.yandex.practicum.filmorate.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void start() {
        userController = new UserController();
    }

    @Test
    void validateUserNegative() {
        User user = User.builder()
                .name("")
                .login("Test")
                .email("test@test.ru")
                .birthday(LocalDate.of(1990,06,01)).build();
        userController.createUser(user);
        assertEquals(user.getName(),user.getLogin());
    }

    @Test
    void validateUserNull() {
        User user = User.builder()
                .name(null)
                .login("Test")
                .email("test@test.ru")
                .birthday(LocalDate.of(1990,06,01)).build();
        userController.createUser(user);
        assertEquals(user.getName(),user.getLogin());
    }
}