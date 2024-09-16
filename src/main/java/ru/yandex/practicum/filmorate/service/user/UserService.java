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

    //private final static Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Practicum.class);
    //logging.level.org.zalando.logbook: TRACE
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public User createUser(User user) {
        log.info("Пришел запрос на создание пользователя с login = {}",user.getLogin());
        return userStorage.createUser(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Optional<User> findById(int id) {
        return userStorage.findById(id);
    }

    public User addInFriends(int id, int friendId) {
        return userStorage.addInFriends(id, friendId);
    }

    public User deletefromFriends(int id, int friendId) {
        return userStorage.deleteFromFriends(id, friendId);
    }

    public List<User> getFriends(int id) {
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(int id,int otherId) {
        return userStorage.getCommonFriends(id,otherId);
    }
}
