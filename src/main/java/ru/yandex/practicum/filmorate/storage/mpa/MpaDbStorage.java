package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("name"))
        );
    }

    @Override
    public Mpa getMpaById(Integer mpaId) throws MpaNotFoundException {
        if (mpaId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        try {
            Mpa mpa = jdbcTemplate.queryForObject("SELECT * FROM mpa_rating WHERE mpa_id = ?",
                    new Object[]{mpaId}, (rs, rowNum) ->
                            new Mpa(
                                    rs.getInt("mpa_id"),
                                    rs.getString("name")
                            ));
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException("Рейтинг с ID=" + mpaId + " не найден!");
        }
    }
}
