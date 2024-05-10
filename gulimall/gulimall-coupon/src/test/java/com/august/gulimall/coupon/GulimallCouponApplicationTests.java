package com.august.gulimall.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//@SpringBootTest
class GulimallCouponApplicationTests {

	@Test
	void contextLoads() {
		LocalDateTime startTime = LocalDate.now().atStartOfDay();
		LocalDate endOfDay = LocalDate.now().plusDays(3);
		LocalDateTime endTime = LocalDateTime.of(endOfDay, LocalTime.MAX);
		startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		System.out.println(startTime);
		System.out.println(endTime);
	}

}
