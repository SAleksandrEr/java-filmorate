package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User> {
    @Override
    public void validate(User data) {
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
            log.info("Display name is empty - login will be used - {} ", data.getName());
        }
    }

    @PostMapping
    public User createFilm(@Valid() @RequestBody User user) {
        super.making(user);
        log.info("The user was created {}",user);
            return user;
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {
        super.update(user);
        log.info("The user was update {}",user);
            return user;
    }

    @GetMapping
    public List<User> getAllFilm() {
        List<User> list = super.getAll();
        log.info("The film was get all {}", list);
            return list;
    }
}
