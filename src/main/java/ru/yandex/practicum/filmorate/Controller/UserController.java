package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid() @RequestBody User user) {
            return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

            return userStorage.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }


    @PutMapping("/{id}/friends/{friendId}")
    public User createdFriendsId(@PathVariable("id") Optional<Long> id,
                                            @PathVariable("friendId") Optional<Long> friendId) {
        if (id.isPresent() & friendId.isPresent()) {
            return userService.createdFriendsId(id.get(),friendId.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + id.get() + " " + friendId.get());
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriendsId(@PathVariable("id") Optional<Long> id,
                                @PathVariable("friendId") Optional<Long> friendId) {
        if (id.isPresent() & friendId.isPresent()) {
            return userService.deleteFriendsId(id.get(),friendId.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + id.get() + " " + friendId.get());
        }
    }

    @GetMapping("/{id}/friends")
    public List<User> findUsersFriendsId(@PathVariable("id") Optional<Long> id) {
        if (id.isPresent()) {
            return userService.findUsersFriendsId(id.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + id.get());
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findUsersOtherId(@PathVariable("id") Optional<Long> id,
                                       @PathVariable("otherId") Optional<Long> otherId) {
        if (id.isPresent() & otherId.isPresent()) {
            return userService.findUsersOtherId(id.get(),otherId.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + id.get() + " " + otherId.get());
        }
    }

    @GetMapping("/{id}")
    public User findUsersId(@PathVariable("id") Optional<Long> id) {
        if (id.isPresent()) {
            return userService.findUsersId(id.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + id.get());
        }
    }
}
