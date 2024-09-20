package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    FilmStorage filmStorage = new FilmDBStorage(jdbcTemplate);
    UserStorage userStorage = new UserDBStorage(jdbcTemplate);
    FilmService filmService = new FilmService(userStorage, filmStorage);
    private final FilmController filmController = new FilmController(filmService);
    private Film film1;
    private User user1;

    @BeforeEach
    public void beforeEach() {
        film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Desc1");
        film1.setReleaseDate(LocalDate.of(2010,2,1));
        film1.setDuration(120);
    }

    @Test
    public void createCorrectFilm() {
        assertEquals(film1,filmController.createFilm(film1), "Возвращаемый объект не соответствует добавляемому");
    }

    @Test
    public void createUncorrectFilm() {
        film1.setReleaseDate(LocalDate.of(1890,12,11));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film1), "Должно вызываться ValidationException");
    }

    @Test
    public void getFilms() {
        int n = filmController.getAll().size();
        filmController.createFilm(film1);
        assertEquals(n + 1, filmController.getAll().size(), "При добавлении количество фильмов не совпадает");
        Film film2 = new Film();
        film2.setId(film1.getId());
        film2.setName("Film4");
        film2.setDescription("Desc4");
        film2.setReleaseDate(LocalDate.of(2001,10,30));
        film2.setDuration(100);
        filmController.updateFilm(film2);
        assertEquals(n + 1, filmController.getAll().size(), "При обновлении количество фильмов не совпадает");
    }

    @Test
    public void findFilm() {
        filmController.createFilm(film1);
        assertEquals(Optional.of(film1), filmController.findById(film1.getId()), "Фильмы не совпадают");
    }

    @Test
    public void changeLikesFilm() {
        filmController.createFilm(film1);
        user1 = new User();
        user1.setName("Mariya");
        user1.setLogin("Mariya2001");
        user1.setEmail("Mariya2001@mail.ru");
        user1.setBirthday(LocalDate.of(2001,4,12));
        userStorage.createUser(user1);
        filmController.likeFilm(film1.getId(),user1.getId());
        assertTrue(film1.getLikes().contains(user1.getId()), "Ошибка при постановке лайка");
        filmController.deleteLike(film1.getId(),user1.getId());
        assertFalse(film1.getLikes().contains(user1.getId()), "Ошибка при удалении лайка");
    }

    @Test
    public void getPopularFilms() {
        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc2");
        film2.setReleaseDate(LocalDate.of(2011,12,28));
        film2.setDuration(90);
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        user1 = new User();
        user1.setName("Mariya");
        user1.setLogin("Mariya2001");
        user1.setEmail("Mariya2001@mail.ru");
        user1.setBirthday(LocalDate.of(2001,4,12));
        User user2 = new User();
        user2.setName("Sophia");
        user2.setLogin("Sophia3");
        user2.setEmail("Sophia1995@mail.ru");
        user2.setBirthday(LocalDate.of(1995,2,20));
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        filmController.likeFilm(film1.getId(),user1.getId());
        filmController.likeFilm(film1.getId(),user2.getId());
        filmController.likeFilm(film2.getId(),user2.getId());
        assertTrue(filmController.getPopularFilms(1).contains(film1));
    }
}