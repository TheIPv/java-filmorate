package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
	private final UserDbStorage userStorage;
	private final JdbcTemplate jdbcTemplate;

	@Test
	public void testFindUserById() throws NotFoundException {

		userStorage.addUser(new User(
				Long.valueOf(1),
				"a@a.ru",
				"hi",
				"me",
				LocalDate.of(2022, 2, 2),
				new HashSet<>()
		));

		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(Long.valueOf(1)));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", Long.valueOf(1))
				);
	}
	@AfterEach
	public void afterAll() {
		jdbcTemplate.update("DELETE FROM users");
		userStorage.zeroUniqueId();
	}
}