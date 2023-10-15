package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private long generationId = 0;
    private final Map<Long, User> storage = new HashMap<>();

    @PostMapping
    public User createFilm(@Valid() @RequestBody User user) {
        validate(user);
        user.setId(generationIdUnit());
        storage.put(user.getId(), user);
        log.info("The user was created {}",user);
            return user;
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {
        if (storage.get(user.getId()) != null) {
            validate(user);
            storage.put(user.getId(), user);
            log.info("The user was update {}",user);
        } else {
            log.warn("Exception, Data not found  id:{}", user.getId());
            throw new DataNotFoundException("Data not found " + user.getId());
        }
            return user;
    }

    @GetMapping
    public List<User> getAllFilm() {
        List<User> list = new ArrayList<>(storage.values());
        log.info("The film was get all {}", list);
        return list;
    }

    protected void validate(User data) {
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
            log.info("Display name is empty - login will be used - {} ", data.getName());
        }
    }

    private long generationIdUnit() {
        return ++generationId;
    }
}
