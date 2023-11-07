package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUsers(User user) {
        validate(user);
        return userStorage.createUser(user);
    }

    public User updateUsers(User user) {
        validate(user);
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUser();
    }

    public User createdFriendsId(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new DataNotFoundException("UserID");
        }
        User user = userStorage.getUsersId(id);
        User userFriend = userStorage.getUsersId(friendId);
        user.setFriends(friendId);
        userFriend.setFriends(id);
        log.info("Created as a friend {}", user);
        return user;
    }

    public User deleteFriendsId(Long id, Long friendId) {
        User user = userStorage.getUsersId(id);
        user.removeFriendsId(friendId);
        log.info("The user deleted the friend - {}", friendId);
        return user;
    }

    public List<User> findUsersFriendsId(Long id) {
        List<User> results = new ArrayList<>();
        for (Long userList : userStorage.getUsersId(id).getFriends()) {
            results.add(userStorage.getUsersId(userList));
        }
        log.info("We return a list of users who are his friends ID {} ", id);
        return results;
    }

    public List<User> findUsersOtherId(Long id, Long otherId) {
        Set<Long> userId = userStorage.getUsersId(id).getFriends();
        Set<Long> userFriendId = userStorage.getUsersId(otherId).getFriends();
        userId.retainAll(userFriendId);
        List<User> results = new ArrayList<>();
        for (Long userList : userId) {
            results.add(userStorage.getUsersId(userList));
        }
        log.info("Displays a list of friends shared with another user ID {} ", id);
        return results;
    }

    public User findUsersId(Long id) {
        User user = userStorage.getUsersId(id);
        log.info("The user was get of ID {} ", id);
        return user;
    }

    private void validate(User data) {
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
            log.info("Display name is empty - login will be used - {} ", data.getName());
        }
    }
}


