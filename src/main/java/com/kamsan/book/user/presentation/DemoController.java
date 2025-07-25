package com.kamsan.book.user.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("demo")
@RequiredArgsConstructor
public class DemoController {

	@GetMapping
	public ResponseEntity<String> demo() {
		return ResponseEntity.ok().body("demo controller");
	}
}
