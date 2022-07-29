package com.aquiris.miniredis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MiniredisApplicationTests {

	@Test
	void contextLoads() {
		assertEquals("1", new String("1"));
	}

}
