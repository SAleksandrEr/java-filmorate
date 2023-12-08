package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventsStorage;
import ru.yandex.practicum.filmorate.dao.FriendStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
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
        log.info("Пользователь был создан с ID {}", user.getId());
        return user;
    }

    public User updateUsers(User user) {
        validate(user);
        user = userStorage.updateUser(user);
        log.info("Пользователь был обновлен {}",user);
        return user;
    }

    public List<User> getAllUsers() {
        List<User> listUser = userStorage.getAllUser();
        log.info("Все пользователи были получены {}", listUser.size());
        return listUser;
    }

    public List<Friend> createdFriendsId(Long userId, Long friendId) {
        findUsersId(userId);
        findUsersId(friendId);
        List<Friend> friends = friendStorage.createFriendUser(friendId, userId);
        eventsStorage.createUserIdEvents(friendId, userId, EventType.FRIEND, EventOperation.ADD);
        log.info("Добавлен друг {}", friends);
        return friends;
    }

    public boolean deleteFriendsId(Long userId, Long friendId) {
        if (friendStorage.deleteFriendsId(userId, friendId)) {
            eventsStorage.createUserIdEvents(friendId, userId, EventType.FRIEND, EventOperation.REMOVE);
            log.info("Пользователь удалил друга - {}", userId + " " + friendId);
            return true;
        } else {
            return false;
        }
    }

    public List<User> findUsersFriendsId(Long userId) {
        findUsersId(userId);
        List<User> results = friendStorage.findFriendUserId(userId);
        log.info("Получен список друзей, у пользователя с ID {} ", userId);
        return results;
    }

    public List<User> findUsersOtherId(Long id, Long otherId) {
        findUsersId(id);
        findUsersId(otherId);
        List<User> results = friendStorage.findUsersOtherId(id, otherId);
        log.info("Список друзей, общий с другим пользователем с ID {} ", id + " и " + otherId);
        return results;
    }

    public User findUsersId(Long id) {
        User user = userStorage.getUsersId(id);
        log.info("Получен пользователь с ID {} ", id);
        return user;
    }

    public void userDeleteById(Long id) { //метод удаления пользователя по id
        userStorage.deleteUserById(id);
        log.info("вызван метод deleteUser - запрос на удаление пользователя с id " + id);
    }

    public List<Event> findUserIdEvents(Long id) {
        findUsersId(id);
        log.info("Список событий у UserID {} ", id);
        return eventsStorage.findUserIdEvents(id);
    }

    private void validate(User data) {
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
            log.info("Отображаемое имя пустое - Будет использован логин - {} ", data.getName());
        }
    }
}