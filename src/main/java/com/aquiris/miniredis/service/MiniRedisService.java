package com.aquiris.miniredis.service;

import com.aquiris.miniredis.entity.NElement;
import com.aquiris.miniredis.entity.SortedSet;
import com.aquiris.miniredis.entity.ZElement;
import com.aquiris.miniredis.repository.NElementRepository;
import com.aquiris.miniredis.repository.SortedSetRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public String getValue(String key) throws NoSuchElementException {
        Optional<NElement> nElement = nElementRepository.findById(key);
        return nElement.map(NElement::getValor).orElseThrow();
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

    public Integer zCardinality(String key) {
        Optional<SortedSet> sortedSet = sortedSetRepository.findById(key);
        return sortedSet.map(set -> set.getElementos().size()).orElse(0);
    }

    private LinkedList<ZElement> orderZElementsByScoreThenMember(List<ZElement> zElements) {
        Comparator<ZElement> comparator = Comparator.comparing(ZElement::getScore);
        comparator = comparator.thenComparing(ZElement::getMember);

        Stream<ZElement> zElementsStream = zElements.stream().sorted(comparator);

        return new LinkedList<>(zElementsStream.collect(Collectors.toList()));
    }

    public Integer zRankMember(String key, String member) throws NoSuchElementException {
        SortedSet sortedSet = sortedSetRepository.findById(key).orElseThrow();

        List<ZElement> zElements = sortedSet.getElementos();
        ZElement wantedElement = findZElement(zElements, member);

        LinkedList<ZElement> zElementsInOrder = orderZElementsByScoreThenMember(zElements);
        return zElementsInOrder.indexOf(wantedElement);
    }

    private ZElement findZElement(List<ZElement> zElements, String member) {
        List<ZElement> foundElements = zElements.stream()
                .filter(zElement -> Objects.equals(zElement.getMember(), member))
                .toList();
        if (foundElements.isEmpty()) throw new NoSuchElementException();
        return foundElements.get(0);
    }

    public List<ZElement> zRangeKey(String key, Integer start, Integer stop) throws NoSuchElementException {
        SortedSet sortedSet = sortedSetRepository.findById(key).orElseThrow();

        List<ZElement> zElements = sortedSet.getElementos();
        LinkedList<ZElement> zElementsInOrder = orderZElementsByScoreThenMember(zElements);

        return findSubList(zElementsInOrder, start, stop);
    }

    private LinkedList<ZElement> findSubList(LinkedList<ZElement> zElements, Integer start, Integer stop) {
        if (start > stop || start > zElements.size() - 1) return new LinkedList<>();
        if (start < 0) start = 0;
        if (stop > zElements.size() - 1) stop = zElements.size() - 1;
        stop++;

        return new LinkedList<>(zElements.subList(start, stop));
    }

}
