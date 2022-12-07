package com.example.member;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// @SpringBootTest
class MemberApplicationTests {

	@Test
	public void testPswEncoder() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encode = passwordEncoder.encode("123456");
		// $2a$10$IX1y4bNhu4Xnzea5dbwLVeoqdx4G5j2yGhQogDM5PUuHTX6F0HkVa
		// $2a$10$U66gkdQXGwMdLrKKFbYnvuRltAELx9tPkA5OcSWRNLB2RuajY7RFa
		boolean m = passwordEncoder.matches("123456", "$2a$10$U66gkdQXGwMdLrKKFbYnvuRltAELx9tPkA5OcSWRNLB2RuajY7RFa");
		System.out.println(encode + "=>" + m);
	}

	@Test
	void contextLoads() {
	}

}
