package com.aquiris.miniredis.service;

import com.aquiris.miniredis.entity.NElement;
import com.aquiris.miniredis.entity.SortedSet;
import com.aquiris.miniredis.entity.ZElement;
import com.aquiris.miniredis.repository.NElementRepository;
import com.aquiris.miniredis.repository.SortedSetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MiniRedisService {
    protected final NElementRepository nElementRepository;
    protected final SortedSetRepository sortedSetRepository;

    MiniRedisService(NElementRepository nElementRepository, SortedSetRepository sortedSetRepository) {
        this.nElementRepository = nElementRepository;
        this.sortedSetRepository = sortedSetRepository;
    }

    public Long getDBSize(String database) {
        return switch (database) {
            case "nElement" -> nElementRepository.count();
            case "sortedSet" -> sortedSetRepository.count();
            default -> nElementRepository.count() + sortedSetRepository.count();
        };
    }

    public NElement setValue(String key, String value) {
        return nElementRepository.save(new NElement(key, value));
    }

    public NElement setValue(String key, String value, Integer seconds) {
        return nElementRepository.save(new NElement(key, value, dateTimeFromNow(seconds)));
    }

    private LocalDateTime dateTimeFromNow(Integer seconds) {
        return LocalDateTime.now().plusSeconds(seconds);
    }

    private Optional<NElement> findKeyIfNotExpired(String key) {
        Optional<NElement> nElement = nElementRepository.findById(key);
        if (nElementIsExpired(nElement)) {
            nElementRepository.delete(nElement.get());
            return Optional.empty();
        }
        return nElement;
    }

    private boolean nElementIsExpired(Optional<NElement> nElement) {
        return (((nElement.isPresent())
                && nElement.get().getExpiryDate() != null)
                && LocalDateTime.now().isAfter(nElement.get().getExpiryDate()));
    }

    public String getValue(String key) {
        Optional<NElement> nElement = findKeyIfNotExpired(key);
        return nElement.map(NElement::getValor).orElse("(nil)");
    }

    public Integer deleteKeys(List<String> keyList, String database) {
        int numberOfKeysRemoved = 0;
        for (String key : keyList) {
            if (database.equals("nElement") || database.equals("both"))
                numberOfKeysRemoved += deleteKeyFromN(key);
            if (database.equals("sortedSet") || database.equals("both"))
                numberOfKeysRemoved += deleteKeyFromZ(key);
        }
        return numberOfKeysRemoved;
    }

    private Integer deleteKeyFromN(String key) {
        Optional<NElement> nElement = findKeyIfNotExpired(key);
        if (nElement.isPresent()) {
            nElementRepository.deleteById(key);
            return 1;
        }
        return 0;
    }

    private Integer deleteKeyFromZ(String key) {
        Optional<SortedSet> sortedSet = sortedSetRepository.findById(key);
        if (sortedSet.isPresent()) {
            sortedSetRepository.deleteById(key);
            return 1;
        }
        return 0;
    }

    public String increaseValue(String key) throws NumberFormatException {
        Optional<NElement> nElement = findKeyIfNotExpired(key);
        long value = 0L;
        if (nElement.isPresent()) {
            value = Long.parseLong(nElement.get().getValor());
        }
        value += 1L;
        return nElementRepository.save(new NElement(key, Long.toString(value))).getValor();
    }


    public Integer zAddScoreMember(String key, List<ZElement> zElements) {
        zElements = zElements.stream().distinct().toList();
        Optional<SortedSet> sortedSet = sortedSetRepository.findById(key);

        if (sortedSet.isPresent()) {
            return addZElements(sortedSet.get(), zElements);
        } else {
            sortedSetRepository.save(new SortedSet(key, zElements));
            return zElements.size();
        }
    }

    private Integer addZElements(SortedSet sortedSet, List<ZElement> zElements) {
        List<ZElement> currentElements = sortedSet.getElementos();
        List<ZElement> updatedElements = new ArrayList<>();
        int numberOfElementsAdded = 0;

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

        return zElementsStream.collect(Collectors.toCollection(LinkedList::new));
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

    private List<ZElement> findSubList(LinkedList<ZElement> zElements, Integer start, Integer stop) {
        if (start > stop || start > zElements.size() - 1) return new ArrayList<>();
        if (start < 0) start = 0;
        if (stop > zElements.size() - 1) stop = zElements.size() - 1;
        stop++;

        return zElements.subList(start, stop);
    }

    public String orchestrateCommandLine(String commandLine) {
        String[] terms = commandLine.split(" ");
        switch (terms[0]) {
            case "SET":
                if (terms[3].equals("EX"))
                    return setValue(terms[1], terms[2], Integer.valueOf(terms[4])).toString();
                return setValue(terms[1], terms[2]).toString();
            case "GET":
                return getValue(terms[1]);
            case "DEL":
                return deleteKeys(termsToKeyList(terms), "both").toString();
            case "INCR":
                return increaseValue(terms[1]);
            case "ZADD":
                return zAddScoreMember(terms[1], termsToScoreMemberList(terms)).toString();
            case "ZCARD":
                return zCardinality(terms[1]).toString();
            case "ZRANK":
                return zRankMember(terms[1], terms[2]).toString();
            case "ZRANGE":
                return zRangeKey(terms[1], Integer.valueOf(terms[2]), Integer.valueOf(terms[3])).toString();
            default:
                return getDBSize("both").toString();
        }
    }

    private List<String> termsToKeyList(String[] terms) {
        return Arrays.asList(terms).subList(1, terms.length - 1);
    }

    private List<ZElement> termsToScoreMemberList(String[] terms) {
        List<ZElement> zElements = new ArrayList<>();
        List<String> scoreAndMembers = Arrays.asList(terms).subList(2, terms.length - 1);

        for (int i = 0; i < scoreAndMembers.size(); i += 2) {
            zElements.add(new ZElement(scoreAndMembers.get(i), Long.valueOf(scoreAndMembers.get(i + 1)), null));
        }

        return zElements;
    }

}
