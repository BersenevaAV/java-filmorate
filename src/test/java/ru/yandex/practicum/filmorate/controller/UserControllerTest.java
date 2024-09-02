package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private final UserController userController = new UserController();
    private User user1;

    @BeforeEach
    public void beforeEach() {
        user1 = new User();
    }

    @Test
    public void createCorrectUser() {
        user1.setName("Mariya");
        user1.setLogin("Mariya2001");
        user1.setEmail("Mariya2001@mail.ru");
        user1.setBirthday(LocalDate.of(2001,4,12));
        assertEquals(user1,userController.createUser(user1), "Возвращаемый объект не соответствует добавляемому");
    }

    @Test
    public void createUncorrectUser() {
        user1.setName("Mariya");
        user1.setLogin("Mariya2");
        user1.setEmail("Mariya2mail.ru");
        user1.setBirthday(LocalDate.of(2001,4,12));
        assertThrows(ValidationException.class, () -> userController.createUser(user1), "Должно вызываться ValidationException");
    }

    @Test
    public void getUsers() {
        user1.setName("Mariya");
        user1.setLogin("Mariya3");
        user1.setEmail("Mariya2001@mail.ru");
        user1.setBirthday(LocalDate.of(2001,4,12));
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
}