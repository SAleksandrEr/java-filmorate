package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private Long generationId = 0L;

    private final Map<Long, User> storage = new HashMap<>();

    public User createUser(User user) {
        user.setId(generationIdUnit());
        storage.put(user.getId(), user);
        log.info("The user was created {}",user);
        return user;
    }
    public User updateUser(User user) {
        if (storage.get(user.getId()) != null) {
            storage.put(user.getId(), user);
            log.info("The user was update {}",user);
        } else {
            log.warn("Exception, Data not found  id:{}", user.getId());
            throw new DataNotFoundException("Data not found " + user.getId());
        }
        return user;
    }
    public List<User> getAllUser() {
        List<User> list = new ArrayList<>(storage.values());
        log.info("The film was get all {}", list);
        return list;
    }
    public User getUsersId(Long id) {
        User user = storage.get(id);
        if (user == null) {
            throw new DataNotFoundException("UserID");
        }
        log.info("The user was get User Id {}", id);
        return user;
    }

    private Long generationIdUnit() {
        return ++generationId;
    }
}
