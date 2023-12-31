package ru.yandex.practicum.filmorate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RecommendationService recommendationService;

    @Autowired
    public UserController(UserService userService, RecommendationService recommendationService) {
        this.userService = userService;
        this.recommendationService = recommendationService;
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
            throw new DataNotFoundException("UserID и FriendId не могут быть одинаковыми");
        }
        return userService.createdFriendsId(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFriendsId(@Valid @PathVariable("id") Long id,
                                   @PathVariable("friendId") Long friendId) {
        return userService.deleteFriendsId(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findUsersFriendsId(@PathVariable("id") Long id) {
        return userService.findUsersFriendsId(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findUsersOtherId(@PathVariable("id") Long id,
                                       @PathVariable("otherId") Long otherId) {
        return userService.findUsersOtherId(id, otherId);
    }

    @GetMapping("/{id}")
    public User findUsersId(@PathVariable("id") Long id) {
        return userService.findUsersId(id);
    }

    @DeleteMapping("/{id}") //удаление пользователя по id
    public void userDeleteById(@PathVariable("id") Long id) {
        userService.userDeleteById(id);
    }

    @GetMapping("/{id}/recommendations") // рекомендация для пользователя по id
    public List<Film> getFilmRecommendations(@PathVariable Long id) {
        return recommendationService.getFilmRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> findUserIdEvents(@PathVariable("id") Long id) {
        return userService.findUserIdEvents(id);
    }
}