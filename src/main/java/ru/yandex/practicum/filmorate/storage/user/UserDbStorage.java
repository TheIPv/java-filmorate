package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private long uniqueId = 1;
    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Collection<User> homeUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                null)
        );    }

    @Override
    public User addUser(User user) {
        if(!user.getEmail().contains("@") || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("Ошибка при вводе электронной почты");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if(user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(uniqueId); ++uniqueId;
        jdbcTemplate.update("INSERT INTO USERS(USER_ID, EMAiL, LOGIN, NAME, BIRTHDAY) VALUES(?,?,?,?,?)",
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        log.info("Добавлен новый пользователь с ID={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if(user.getId() >= uniqueId || user.getId() <= 0) {
            throw new NotFoundException("Вы пытаетесь изменить несуществующего пользователя");
        }
        if(!user.getEmail().contains("@") || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("Ошибка при вводе электронной почты");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if(user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (getUserById(user.getId()) != null) {
            String sqlQuery = "UPDATE users SET " +
                    "email = ?, login = ?, name = ?, birthday = ? " +
                    "WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("Пользователь с ID={} успешно обновлен", user.getId());
            return user;
        } else {
            throw new NotFoundException("Пользователь с ID=" + user.getId() + " не найден!");
        }
    }
    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", userId);
        if (userRows.first()) {
            user = new User(
                    userRows.getLong("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate(),
                    null);
        } else {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        return user;
    }
    public void zeroUniqueId() {
        uniqueId = 1;
    }
    @Override
    public Map<Long, User> getUsers() {
        return null;
    }
}
