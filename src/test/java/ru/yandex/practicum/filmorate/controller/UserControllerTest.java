package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    FilmStorage filmStorage = new FilmDBStorage(jdbcTemplate);
    UserStorage userStorage = new UserDBStorage(jdbcTemplate);
    UserService userService = new UserService(userStorage,filmStorage);
    private final UserController userController = new UserController(userService);
    private User user1;

    @BeforeEach
    public void beforeEach() {
        user1 = new User();
        user1.setName("Mariya");
        user1.setLogin("Mariya2001");
        user1.setEmail("Mariya2001@mail.ru");
        user1.setBirthday(LocalDate.of(2001,4,12));
    }

    @Test
    public void createCorrectUser() {
        assertEquals(user1,userController.createUser(user1), "Возвращаемый объект не соответствует добавляемому");
    }

    @Test
    public void createUncorrectUser() {
        user1.setEmail("Mariya2mail.ru");
        assertThrows(ValidationException.class, () -> userController.createUser(user1), "Должно вызываться ValidationException");
    }

    @Test
    public void getUsers() {
        int n = userController.getAll().size();
        userController.createUser(user1);
        assertEquals(n + 1, userController.getAll().size(), "При добавлении количество пользователей не совпадает");
        User user2 = new User();
        user2.setId(user1.getId());
        user2.setName("Sophia");
        user2.setLogin("Sophia3");
        user2.setEmail("Sophia1995@mail.ru");
        user2.setBirthday(LocalDate.of(1995,2,20));
        userController.updateUser(user2);
        assertEquals(n + 1, userController.getAll().size(), "При обновлении количество пользователей не совпадает");
    }

    @Test
    public void findUser() {
        userController.createUser(user1);
        assertEquals(Optional.of(user1), userController.findById(user1.getId()), "Пользователи не совпадают");
    }

    @Test
    public void changeFriends() {
        User user2 = new User();
        user2.setName("Sophia");
        user2.setLogin("Sophia3");
        user2.setEmail("Sophia1995@mail.ru");
        user2.setBirthday(LocalDate.of(1995,2,20));
        userController.createUser(user1);
        userController.createUser(user2);
        userController.addInFriends(user1.getId(),user2.getId());
        assertTrue(user1.getFriends().contains(user2.getId()),"Друг user2 не добавлен ");
        assertTrue(user2.getFriends().contains(user1.getId()),"Друг user1 не добавлен ");
        userController.deletefromFriends(user1.getId(),user2.getId());
        assertFalse(user1.getFriends().contains(user2.getId()),"Друг user2 не удален");
        assertFalse(user2.getFriends().contains(user1.getId()),"Друг user1 не удален");
    }

    @Test
    public void getCommonFriends() {
        User user2 = new User();
        user2.setName("Sophia");
        user2.setLogin("Sophia3");
        user2.setEmail("Sophia1995@mail.ru");
        user2.setBirthday(LocalDate.of(1995,2,20));
        User user3 = new User();
        user3.setName("Pavel");
        user3.setLogin("Pavel2001");
        user3.setEmail("Pavel2001@mail.ru");
        user3.setBirthday(LocalDate.of(2001,9,15));
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        userController.addInFriends(user1.getId(),user2.getId());
        userController.addInFriends(user3.getId(),user2.getId());
        assertTrue(userController.getCommonFriends(user1.getId(),user3.getId()).contains(user2));
    }
}