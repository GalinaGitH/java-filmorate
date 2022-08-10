package ru.yandex.practicum.filmorate.DAO;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    public void getByIdTest() {
        Mpa mpa = mpaStorage.getById(1);
        assertEquals("G", mpa.getName());
    }
    @Test
    public void getAllMpaTest() {
        assertEquals(5, mpaStorage.getAll().size());
    }
}
