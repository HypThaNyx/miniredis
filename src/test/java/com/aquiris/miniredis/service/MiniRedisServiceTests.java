package com.aquiris.miniredis.service;

import com.aquiris.miniredis.entity.NElement;
import com.aquiris.miniredis.entity.SortedSet;
import com.aquiris.miniredis.entity.ZElement;
import com.aquiris.miniredis.repository.NElementRepository;
import com.aquiris.miniredis.repository.SortedSetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class MiniRedisServiceTests {

    @InjectMocks
    MiniRedisService miniRedisService;

    @Mock
    NElementRepository nElementRepository;

    @Mock
    SortedSetRepository sortedSetRepository;

    @Test
    @DisplayName("Should get DBSizes")
    void shouldGetDbSizes() {
        when(nElementRepository.count()).thenReturn(1L);
        when(sortedSetRepository.count()).thenReturn(1L);

        Long keysFromN = miniRedisService.getDBSize( "nElement");
        Long keysFromZ = miniRedisService.getDBSize("sortedSet");
        Long keysFromBoth = miniRedisService.getDBSize("both");

        assertEquals(1, keysFromN);
        assertEquals(1, keysFromZ);
        assertEquals(2, keysFromBoth);
    }

    @Test
    @DisplayName("Should set key value")
    void shouldSetKeyValue() {
        String key = "testKey";
        String value = "123";
        NElement mockElement = new NElement(key, value);

        when(nElementRepository.save(any())).thenReturn(mockElement);

        NElement actualElement = miniRedisService.setValue(key, value);

        assertEquals(key, actualElement.getChave());
        assertEquals(value, actualElement.getValor());
    }

    @Test
    @DisplayName("Should set key value with expiration time")
    void shouldSetKeyValueEXSeconds() {
        String key = "testKey";
        String value = "123";
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(10);

        NElement mockElement = new NElement(key, value, expiryDate);

        when(nElementRepository.save(any())).thenReturn(mockElement);

        NElement actualElement = miniRedisService.setValue(key, value, 10);

        assertEquals(key, actualElement.getChave());
        assertEquals(value, actualElement.getValor());
        assertNotNull(actualElement.getExpiryDate());
    }

    @Test
    @DisplayName("Should get key value")
    void shouldGetKeyValue() {
        String key = "testKey";
        String value = "123";
        NElement elementMock = new NElement(key, value);

        when(nElementRepository.findById("testKey")).thenReturn(Optional.of(elementMock));
        when(nElementRepository.save(any())).thenReturn(elementMock);

        miniRedisService.setValue(key, value);
        String actualValue = miniRedisService.getValue(key);

        assertEquals(value, actualValue);
    }

    @Test
    @DisplayName("Should not find expired key value")
    void shouldNotFindExpiredKeyValue() {
        String key = "testKey";
        String value = "123";
        LocalDateTime expiredExpiryDate = LocalDateTime.now().minusMinutes(10);
        NElement elementMock = new NElement(key, value, expiredExpiryDate);

        when(nElementRepository.findById("testKey")).thenReturn(Optional.of(elementMock));
        doNothing().when(nElementRepository).delete(any());

        String expectedValue = "(nil)";
        String actualValue = miniRedisService.getValue(key);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @DisplayName("Should succeed deleting from Databases")
    void shouldSucceedDeletingFromDatabases() {
        when(nElementRepository.findById("testKey")).thenReturn(Optional.of(new NElement()));
        doNothing().when(nElementRepository).deleteById(any());
        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.of(new SortedSet()));
        doNothing().when(sortedSetRepository).deleteById(any());

        Integer removedKeysFromN = miniRedisService.deleteKeys(new ArrayList<>(Collections.singleton("testKey")), "nElement");
        Integer removedKeysFromZ = miniRedisService.deleteKeys(new ArrayList<>(Collections.singleton("testKey")), "sortedSet");
        Integer removedKeysFromBoth = miniRedisService.deleteKeys(new ArrayList<>(Collections.singleton("testKey")), "both");

        assertEquals(1, removedKeysFromN);
        assertEquals(1, removedKeysFromZ);
        assertEquals(2, removedKeysFromBoth);
    }

    @Test
    @DisplayName("Should fail deleting from Databases")
    void shouldFailDeletingFromDatabases() {
        when(nElementRepository.findById("testKey")).thenReturn(Optional.empty());
        doNothing().when(nElementRepository).deleteById(any());
        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.empty());
        doNothing().when(sortedSetRepository).deleteById(any());

        Integer removedKeysFromN = miniRedisService.deleteKeys(new ArrayList<>(Collections.singleton("testKey")), "nElement");
        Integer removedKeysFromZ = miniRedisService.deleteKeys(new ArrayList<>(Collections.singleton("testKey")), "sortedSet");
        Integer removedKeysFromBoth = miniRedisService.deleteKeys(new ArrayList<>(Collections.singleton("testKey")), "both");

        assertEquals(0, removedKeysFromN);
        assertEquals(0, removedKeysFromZ);
        assertEquals(0, removedKeysFromBoth);
    }

    @Test
    @DisplayName("Should increase key value")
    void shouldIncreaseKeyValue() {
        String key = "testKey";
        String value = "123";
        NElement elementMock = new NElement(key, value);

        when(nElementRepository.findById("testKey")).thenReturn(Optional.of(elementMock));
        elementMock.setValor("124");
        when(nElementRepository.save(any())).thenReturn(elementMock);

        String actualValue = miniRedisService.increaseValue(key);
        String expectedValue = String.valueOf(Integer.parseInt(value) + 1);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @DisplayName("Should add score member to key")
    void shouldAddScoreMemberToKey() {
        List<ZElement> zElements = new ArrayList<>();
        zElements.add(new ZElement());
        zElements.add(new ZElement());

        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.empty());
        when(sortedSetRepository.save(any())).thenReturn(new SortedSet());

        Integer addedScoreMembers = miniRedisService.zAddScoreMember("testKey", zElements);

        assertEquals(2, addedScoreMembers);
    }

    @Test
    @DisplayName("Should update key score member")
    void shouldUpdateKeyScoreMember() {
        List<ZElement> oldZElements = new ArrayList<>();
        oldZElements.add(new ZElement("member1", 1L, null));
        oldZElements.add(new ZElement("member2", 2L, null));

        SortedSet sortedSet = new SortedSet("testKey", oldZElements);

        List<ZElement> newZElements = new ArrayList<>();
        newZElements.add(new ZElement("member2", 3L, null));
        newZElements.add(new ZElement("member3", 4L, null));

        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.of(sortedSet));
        when(sortedSetRepository.save(any())).thenReturn(new SortedSet());

        Integer addedScoreMembers = miniRedisService.zAddScoreMember("testKey", newZElements);

        assertEquals(1, addedScoreMembers);
    }

    @Test
    @DisplayName("Should return cardinality one")
    void shouldReturnCardinalityOne() {
        List<ZElement> oldZElements = new ArrayList<>();
        oldZElements.add(new ZElement("member1", 1L, null));
        SortedSet sortedSet = new SortedSet("testKey", oldZElements);

        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.of(sortedSet));

        Integer actualCardinality = miniRedisService.zCardinality("testKey");

        assertEquals(1, actualCardinality);
    }

    @Test
    @DisplayName("Should return cardinality zero")
    void shouldReturnCardinalityZero() {
        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.empty());

        Integer actualCardinality = miniRedisService.zCardinality("testKey");

        assertEquals(0, actualCardinality);
    }

    @Test
    @DisplayName("Should rank key members")
    void shouldRankKeyMembers() {
        List<ZElement> oldZElements = new ArrayList<>();
        oldZElements.add(new ZElement("member1", 1L, null));
        oldZElements.add(new ZElement("member2", 2L, null));
        oldZElements.add(new ZElement("member3", 3L, null));

        SortedSet sortedSet = new SortedSet("testKey", oldZElements);

        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.of(sortedSet));
        when(sortedSetRepository.save(any())).thenReturn(new SortedSet());

        Integer actualMemberRank = miniRedisService.zRankMember("testKey", "member2");

        assertEquals(1, actualMemberRank);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when finding ZElement")
    void shouldThrowNoSuchElementExceptionWhenFindingZElement() {
        List<ZElement> oldZElements = new ArrayList<>();
        oldZElements.add(new ZElement("member1", 1L, null));
        oldZElements.add(new ZElement("member3", 3L, null));

        SortedSet sortedSet = new SortedSet("testKey", oldZElements);

        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.of(sortedSet));
        when(sortedSetRepository.save(any())).thenReturn(new SortedSet());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            miniRedisService.zRankMember("testKey", "member2");
        });

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should return key range")
    void shouldReturnKeyRange() {
        List<ZElement> oldZElements = new ArrayList<>();
        oldZElements.add(new ZElement("member1", 1L, null));
        oldZElements.add(new ZElement("member2", 2L, null));
        oldZElements.add(new ZElement("member3", 3L, null));

        List<ZElement> expectedZElements = new ArrayList<>();
        expectedZElements.add(new ZElement("member1", 1L, null));
        expectedZElements.add(new ZElement("member2", 2L, null));

        SortedSet sortedSet = new SortedSet("testKey", oldZElements);

        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.of(sortedSet));
        when(sortedSetRepository.save(any())).thenReturn(new SortedSet());

        List<ZElement> actualZElements = miniRedisService.zRangeKey("testKey", 0, 1);

        assertEquals(expectedZElements.size(), actualZElements.size());
        assertEquals(expectedZElements.get(0), actualZElements.get(0));
        assertEquals(expectedZElements.get(0).getMember(), actualZElements.get(0).getMember());
        assertEquals(expectedZElements.get(0).getScore(), actualZElements.get(0).getScore());
    }

    @Test
    @DisplayName("Should test findSubList start and stop rules]")
    void shouldTestFindSubListStartAndStopRules() {
        List<ZElement> oldZElements = new ArrayList<>();
        oldZElements.add(new ZElement("member1", 1L, null));
        oldZElements.add(new ZElement("member2", 2L, null));

        List<ZElement> expectedZElements = new ArrayList<>();
        expectedZElements.add(new ZElement("member1", 1L, null));
        expectedZElements.add(new ZElement("member2", 2L, null));

        SortedSet sortedSet = new SortedSet("testKey", oldZElements);

        when(sortedSetRepository.findById("testKey")).thenReturn(Optional.of(sortedSet));
        when(sortedSetRepository.save(any())).thenReturn(new SortedSet());

        List<ZElement> actualZElements = miniRedisService.zRangeKey("testKey", 1, 0);
        assertEquals(0, actualZElements.size());

        actualZElements = miniRedisService.zRangeKey("testKey", 3, 3);
        assertEquals(0, actualZElements.size());

        actualZElements = miniRedisService.zRangeKey("testKey", -1, 5);
        assertEquals(expectedZElements.size(), actualZElements.size());
    }
}
