package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        //new ObjectMapper().registerModule(new JavaTimeModule());

        if (checkNewUser(user) == true) {
            user.setId(idGenerator++);
            users.put(user.getId(),user);
            log.info("Пришел запрос на создание пользователя с login = {}",user.getLogin());
        } else {
            throw new ValidationException("Данные заданы неверно");
        }
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (users.containsKey(user.getId()) && checkNewUser(user) == true) {
            users.put(user.getId(),user);
        } else {
            throw new ValidationException("Данные заданы неверно");
        }
        return user;
    }

    public boolean checkNewUser(User newUser) {
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
