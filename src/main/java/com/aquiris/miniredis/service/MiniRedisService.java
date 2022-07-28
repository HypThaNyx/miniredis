package com.aquiris.miniredis.service;

import com.aquiris.miniredis.entity.NElement;
import com.aquiris.miniredis.entity.SortedSet;
import com.aquiris.miniredis.entity.ZElement;
import com.aquiris.miniredis.repository.NElementRepository;
import com.aquiris.miniredis.repository.SortedSetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public Integer zAddScoreMember(String key, List<ZElement> zElements) {
        zElements = zElements.stream().distinct().collect(Collectors.toList());
        Optional<SortedSet> sortedSet = sortedSetRepository.findById(key);

        if (sortedSet.isPresent()) {
            return addZElements(sortedSet.get(), zElements);
        }
        else {
            sortedSetRepository.save(new SortedSet(key, zElements));
            return zElements.size();
        }
    }

    private Integer addZElements(SortedSet sortedSet, List<ZElement> zElements) {
        List<ZElement> currentElements = sortedSet.getElementos();
        List<ZElement> updatedElements = new ArrayList<>();
        Integer numberOfElementsAdded = 0;

        for (ZElement zElement : zElements) {
            numberOfElementsAdded += addOrUpdateZElement(currentElements, zElement, updatedElements);
        }

        sortedSet.setElementos(updatedElements);
        sortedSetRepository.save(sortedSet);
        return numberOfElementsAdded;
    }

    private Integer addOrUpdateZElement(List<ZElement> currentElements, ZElement zElement, List<ZElement> updatedElements) {
        if (currentElements.contains(zElement)) {
            ZElement currentElement = currentElements.get(currentElements.indexOf(zElement));
            currentElement.setScore(zElement.getScore());
            updatedElements.add(currentElement);
            return 0;
        } else {
            updatedElements.add(zElement);
            return 1;
        }
    }



}
