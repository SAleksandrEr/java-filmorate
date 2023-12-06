package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventsStorage;
import ru.yandex.practicum.filmorate.dao.FriendStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    private final FriendStorage friendStorage;

    private final EventsStorage eventsStorage;

    @Autowired
    public UserService(@Qualifier("userDaoImpl") UserStorage userStorage, FriendStorage friendStorage, EventsStorage eventsStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.eventsStorage = eventsStorage;
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
        eventsStorage.createUserIdEvents(friendId, userId, EventType.FRIEND, EventOperation.ADD);
        log.info("Created as a friend {}", friends);
        return friends;
    }

    public boolean deleteFriendsId(Long userId, Long friendId) {
        if (friendStorage.deleteFriendsId(userId, friendId)) {
            eventsStorage.createUserIdEvents(friendId, userId, EventType.FRIEND, EventOperation.REMOVE);
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
            throw new DataNotFoundException("Такого пользователя не существует");
        }
        userStorage.deleteUserById(id);
    }

    public List<Event> findUserIdEvents(Long id) {
        findUsersId(id);
        log.info("The events was get of UserID {} ", id);
        return eventsStorage.findUserIdEvents(id);
    }
}