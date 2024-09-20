package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet();

    public boolean addFriend(int friendId) {
        return friends.add(friendId);
    }

    public boolean deleteFriend(int friendId) {
        return friends.remove(friendId);
    }
}
