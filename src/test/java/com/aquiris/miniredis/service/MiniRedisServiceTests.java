package com.aquiris.miniredis.service;

import com.aquiris.miniredis.entity.NElement;
import com.aquiris.miniredis.repository.NElementRepository;
import com.aquiris.miniredis.repository.SortedSetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MiniRedisServiceTests {

    @InjectMocks
    MiniRedisService miniRedisService;

    @Mock
    NElementRepository nElementRepository;

    @Mock
    SortedSetRepository sortedSetRepository;
    
    @Test
    @DisplayName("Should set key value")
    void shouldSetKeyValue() {
        String key = "test1";
        String value = "123";
        NElement elementMock = new NElement(key, value);

        when(nElementRepository.save(any())).thenReturn(elementMock);

        NElement elementActual = miniRedisService.setValue(key, value);

        assertEquals(key, elementActual.getChave());
        assertEquals(value, elementActual.getValor());
    }

    @Test
    @DisplayName("Should get key value")
    void shouldGetKeyValue() {
        String key = "test1";
        String value = "123";
        NElement elementMock = new NElement(key, value);

        when(nElementRepository.findById("test1")).thenReturn(Optional.of(elementMock));
        when(nElementRepository.save(any())).thenReturn(elementMock);

        miniRedisService.setValue(key, value);
        String valueActual = miniRedisService.getValue(key);

        assertEquals(value, valueActual);
    }
}
