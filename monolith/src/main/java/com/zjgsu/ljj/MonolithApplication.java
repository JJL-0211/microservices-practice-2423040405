package com.zjgsu.ljj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MonolithApplication {
	public static void main(String[] args) {
		SpringApplication.run(MonolithApplication.class, args);
	}

	// 作业要求的 /api/hello 接口
	@GetMapping("/api/hello")
	public String hello(){
		return "monolith 单体项目";
	}
}