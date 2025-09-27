package com.example.newboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.newboard") // ✅ 패키지 스캔 확실히 명시
public class NewBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewBoardApplication.class, args);
	}
}

