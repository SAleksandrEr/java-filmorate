package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        List<Mpa> listMpa = mpaStorage.getAllMpa();
        log.info("Все mpa получены {}", listMpa);
        return listMpa;
    }

    public Mpa findMpaId(Long id) {
        Mpa mpa = mpaStorage.findMpaId(id);
        log.info("mpa получены по ID {}", id);
        return mpa;
    }
}
