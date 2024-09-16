package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

@Component
public interface UserStorage {
    User createUser(User user);

    List<User> getAll();

    User updateUser(User user);

    Optional<User> findById(int id);

    User addInFriends(int id, int friendId);

    User deleteFromFriends(int id, int friendId);

    List<User> getFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    boolean checkIdUser(int id);
}
