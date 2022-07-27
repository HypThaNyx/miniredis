package com.aquiris.miniredis.service;

import com.aquiris.miniredis.entity.NElement;
import com.aquiris.miniredis.repository.NElementRepository;
import com.aquiris.miniredis.repository.SortedSetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MiniRedisService {
    private final NElementRepository nElementRepository;
    private final SortedSetRepository sortedSetRepository;

    MiniRedisService(NElementRepository nElementRepository, SortedSetRepository sortedSetRepository) {
        this.nElementRepository = nElementRepository;
        this.sortedSetRepository = sortedSetRepository;
    }

    public Integer getDBSize(String database) {
        switch (database) {
            case "nElement":
                return Long.valueOf(nElementRepository.count()).intValue();
            case "sortedSet":
                return Long.valueOf(sortedSetRepository.count()).intValue();
            default:
                return Long.valueOf(nElementRepository.count() + sortedSetRepository.count()).intValue();
        }
    }

    public NElement setValue(String key, String value) {
        return nElementRepository.save(new NElement(key, value));
    }

    public NElement setValue(String key, String value, Integer seconds){
        return nElementRepository.save(new NElement(key, value, dateTimeFromNow(seconds)));
    }

    private LocalDateTime dateTimeFromNow(Integer seconds) {
        return LocalDateTime.now().plusSeconds(seconds);
    }

    public String getValue(String key) {
        Optional<NElement> nElement = nElementRepository.findById(key);
        return nElement.isPresent() ? nElement.get().getValor() : null;
    }

    public Integer deleteKeys(List<String> keyList) {
        Integer numberOfKeysRemoved = 0;
        for (String key : keyList) {
            Optional<NElement> nElement = nElementRepository.findById(key);
            if (nElement.isPresent()) {
                nElementRepository.deleteById(key);
                numberOfKeysRemoved++;
            }
        }
        return numberOfKeysRemoved;
    }

    public String increaseValue(String key) throws NumberFormatException {
        Optional<NElement> nElement = nElementRepository.findById(key);
        Long value = 0L;
        if (nElement.isPresent()) {
            value = Long.valueOf(nElement.get().getValor());
        }
        value += 1L;
        return nElementRepository.save(new NElement(key, Long.toString(value))).getValor();
    }

}
