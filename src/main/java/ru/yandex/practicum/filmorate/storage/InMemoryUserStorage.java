package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private long generationId = 0;

    private final Map<Long, User> storage = new HashMap<>();

    public User createUser(User user) {
        validate(user);
        user.setId(generationIdUnit());
        storage.put(user.getId(), user);
        log.info("The user was created {}",user);
        return user;
    }

    public User updateUser(User user) {
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

    public List<User> getAllUsers(){
        List<User> list = new ArrayList<>(storage.values());
        log.info("The film was get all {}", list);
        return list;
    }

    public User getUsersId(Long id){
        User user = storage.get(id);
        log.info("The user was get User Id {}", id);
        return user;
    }

    public void removeAllUsers() {

    }

    public void removeIdUser(Long id) {

    }

    private long generationIdUnit() {
        return ++generationId;
    }

    private void validate(User data) {
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
            log.info("Display name is empty - login will be used - {} ", data.getName());
        }
    }
}
