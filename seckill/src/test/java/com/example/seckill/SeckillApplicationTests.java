package com.example.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// @SpringBootTest
class SeckillApplicationTests {

	@Test
	void contextLoads() {
		LocalDate now = LocalDate.now();
		LocalTime min = LocalTime.MIN;
		LocalDateTime start = LocalDateTime.of(now, min);
		String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		System.out.println("format = " + format);

		LocalDateTime end = LocalDateTime.of(now.plusDays(2), LocalTime.MAX);
		String format1 = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		System.out.println("format1 = " + format1);
	}

}
