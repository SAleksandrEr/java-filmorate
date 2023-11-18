package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {

    List<User> findFriendUserId(Long id);

    List<Friend> createFriendUser(Long friendId, Long userId);

    List<User> findUsersOtherId(Long id, Long otherId);

    List<Friend> getFriendUserId(Long userId);

    boolean deleteFriendsId(Long userId, Long friendId);
}
