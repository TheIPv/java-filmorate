package ru.yandex.practicum.filmorate.storage.friend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Component
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if ((user != null) && (friend != null)) {
            if (friend.getFriends() == null || !friend.getFriends().contains(userId)) {
                String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
                jdbcTemplate.update(sql, userId, friendId);
            }
        } else throw new ValidationException("Не существующий пользователь");
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if ((user != null) && (friend != null)) {
            String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
            jdbcTemplate.update(sql, userId, friendId, friendId, userId);
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user != null) {
            String sql = "SELECT friend_id, email, login, name, birthday FROM friendships" +
                    " INNER JOIN users ON friendships.friend_id = users.user_id WHERE friendships.user_id = ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                            rs.getLong("friend_id"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getDate("birthday").toLocalDate(),
                            null),
                    userId
            );
        } else {
            return null;
        }
    }
}
