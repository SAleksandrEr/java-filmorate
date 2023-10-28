package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor(force = true)
public class User extends Unit {

    @NonNull
    @Email
    private String email;

    @NotBlank
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

    public void setFriends(Long friendsId) {
        friends.add(friendsId);
    }

    public Set<Long> getFriends() {
        return new HashSet<>(friends);
    }

    public void removeFriendsId(Long friendsId) {
        friends.remove(friendsId);
    }
}
