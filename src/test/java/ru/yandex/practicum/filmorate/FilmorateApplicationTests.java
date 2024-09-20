package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;


@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoads() {
	}

}
