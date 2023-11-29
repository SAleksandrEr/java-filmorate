package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUsers(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUsers(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<Friend> createdFriendsId(@Valid @PathVariable("id") Long id,
                                         @PathVariable("friendId") Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new DataNotFoundException("UserID and FriendId Can't be the same");
        }
            return userService.createdFriendsId(id,friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFriendsId(@Valid @PathVariable("id") Long id,
                                @PathVariable("friendId") Long friendId) {
            return userService.deleteFriendsId(id,friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findUsersFriendsId(@PathVariable("id") Long id) {
            return userService.findUsersFriendsId(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findUsersOtherId(@PathVariable("id") Long id,
                                       @PathVariable("otherId") Long otherId) {
            return userService.findUsersOtherId(id,otherId);
    }

    @GetMapping("/{id}")
    public User findUsersId(@PathVariable("id") Long id) {
            return userService.findUsersId(id);
    }

    @DeleteMapping("/{id}") //удаление пользователя по id
    public void userDeleteById(@PathVariable("id") Long id) {
        log.info("вызван метод deleteUser - запрос на удаление пользователя с id " + id);
        userService.userDeleteById(id);
    }
}
