package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 1;

    @Override
    public User createUser(User user) {
        if (checkNewUser(user) == true) {
            user.setId(idGenerator++);
            users.put(user.getId(),user);
        } else {
            throw new ValidationException("Данные заданы неверно");
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId()) && checkNewUser(user) == true) {
            users.put(user.getId(),user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Объект не найден");
        }
        return user;
    }

    public Optional<User> findById(int id) {
        return users.values().stream()
                .filter(x -> x.getId() == id)
                .findFirst();
    }

    public User addInFriends(int id, int friendId) {
        if (!users.containsKey(friendId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный id друга.");
        if (!users.containsKey(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный id");
        if (users.get(id).addFriend(friendId) == false || users.get(friendId).addFriend(id) == false)
            throw new ValidationException("Друг уже был ранее добавлен");
        return users.get(friendId);
    }

    public User deleteFromFriends(int id, int friendId) {
        if (!users.containsKey(friendId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный id друга.");
        if (!users.containsKey(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный id");
        if (users.get(id).getFriends().contains(friendId) && (users.get(id).deleteFriend(friendId) == false || users.get(friendId).deleteFriend(id) == false))
            throw new ValidationException("Такого друга нет");
        return users.get(friendId);
    }

    public List<User> getFriends(int id) {
        if (!users.containsKey(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный id");
        return users.values().stream()
                .filter(x -> users.get(id).getFriends().contains(x.getId()))
                .toList();
    }

    public List<User> getCommonFriends(int id, int otherId) {
        if (!users.containsKey(id) || !users.containsKey(otherId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный id");
        Set<Integer> result = new HashSet<>(users.get(id).getFriends());
        result.retainAll(users.get(otherId).getFriends());
        return users.values().stream()
                .filter(x -> result.contains(x.getId()))
                .toList();
    }

    public boolean checkIdUser(int id) {
        if (users.containsKey(id))
            return true;
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный id");
    }

    private boolean checkNewUser(User newUser) {
        boolean trueEmail = !newUser.getEmail().isEmpty() && newUser.getEmail().contains("@");
        boolean trueLogin = !newUser.getLogin().isEmpty() && !newUser.getLogin().contains(" ");
        boolean trueBirthday = newUser.getBirthday().isBefore(LocalDate.now());
        if (trueEmail && trueLogin && trueBirthday) {
            if (newUser.getName() == null)
                newUser.setName(newUser.getLogin());
            return true;
        } else
            return false;
    }
}
