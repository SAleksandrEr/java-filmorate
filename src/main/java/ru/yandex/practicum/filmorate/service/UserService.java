package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendStorage;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("userDaoImpl") UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public User createUsers(User user) {
        validate(user);
        user = userStorage.createUser(user);
        log.info("The user was created with ID {}", user.getId());
        return user;
    }

    public User updateUsers(User user) {
        validate(user);
        user = userStorage.updateUser(user);
        log.info("The film was update {}",user);
        return user;
    }

    public List<User> getAllUsers() {
        List<User> listUser = userStorage.getAllUser();
        log.info("The all users was get {}", listUser.size());
        return listUser;
    }

    public List<Friend> createdFriendsId(Long userId, Long friendId) {
        findUsersId(userId);
        findUsersId(friendId);
        List<Friend> friends = friendStorage.createFriendUser(friendId, userId);
        log.info("Created as a friend {}", friends);
        return friends;
    }

    public boolean deleteFriendsId(Long userId, Long friendId) {
        if (friendStorage.deleteFriendsId(userId, friendId)) {
            log.info("The user deleted the friend - {}", friendId);
            return true;
        } else {
            return false;
        }
    }

    public List<User> findUsersFriendsId(Long userId) {
        findUsersId(userId);
        List<User> results = friendStorage.findFriendUserId(userId);
        log.info("We return a list of users who are his friends ID {} ", userId);
        return results;
    }

    public List<User> findUsersOtherId(Long id, Long otherId) {
        findUsersId(id);
        findUsersId(otherId);
        List<User> results = friendStorage.findUsersOtherId(id, otherId);
        log.info("Displays a list of friends shared with another user ID {} ", id + " и " + otherId);
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

    public void userDeleteById(Long id) { //метод удаления пользователя по id
        if (userStorage.getUsersId(id) == null) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        userStorage.deleteUserById(id);
    }
}