package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseController<T extends Unit> {

    private final Map<Long, T> storage = new HashMap<>();
    public static long generationId = 0;

    public T making(T data) {
        validate(data);
        data.setId(generationIdUnit());
        storage.put(data.getId(), data);
            return data;
    }

    public T update(T data) {
        validate(data);
        if (storage.get(data.getId()) != null) {
            storage.put(data.getId(), data);
        } else {
            log.warn("Exception, Data not found  id:{}", data.getId());
            throw new DataNotFoundException("Data not found " + data.getId());
        }
            return data;
    }

    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }
    
    public abstract void validate(T data);
    
    private long generationIdUnit() {
        return ++generationId;
    }
}
