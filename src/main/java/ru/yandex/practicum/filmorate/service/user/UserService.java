package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public User createUser(User user) {
        log.info("Пришел запрос на создание пользователя с login = {}",user.getLogin());
        return userStorage.createUser(user);
    }

    public List<User> getAll() {
        log.info("Пришел запрос для получения всех пользователей");
        return userStorage.getAll();
    }

    public User updateUser(User user) {
        log.info("Пришел запрос на обновление пользователя с id = {}",user.getId());
        return userStorage.updateUser(user);
    }

    public Optional<User> findById(int id) {
        log.info("Пришел запрос на поиск пользователя с id = {}",id);
        return userStorage.findById(id);
    }

    public User addInFriends(int id, int friendId) {
        log.info("Пришел запрос на добавление друга(id={}) к пользователю с id = {}",friendId, id);
        return userStorage.addInFriends(id, friendId);
    }

    public User deletefromFriends(int id, int friendId) {
        log.info("Пришел запрос на удаление друга(id={}) у пользователя с id = {}",friendId, id);
        return userStorage.deleteFromFriends(id, friendId);
    }

    public List<User> getFriends(int id) {
        log.info("Пришел запрос на получение друзей у пользователя с id = {}", id);
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(int id,int otherId) {
        log.info("Пришел запрос на получение общих друзей у пользователей с id = {}, {}", id, otherId);
        return userStorage.getCommonFriends(id,otherId);
    }
}
