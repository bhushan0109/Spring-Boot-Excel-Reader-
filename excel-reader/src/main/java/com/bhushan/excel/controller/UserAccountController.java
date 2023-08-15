package com.bhushan.excel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bhushan.excel.service.UserAccountService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserAccountController {

	private final UserAccountService userAccountService;

	@PostMapping("/withoutAsync")
	public ResponseEntity<?> batchInsertWithoutAsync(@RequestParam("file") MultipartFile multipartFile) {
		userAccountService.batchInsertWithoutAsync(multipartFile);
		return ResponseEntity.ok("File Reading complete without async");
	}

	@PostMapping("/Async")
	public ResponseEntity<?> batchInsertWithoutAsyncWithoutObjectsplit(
			@RequestParam("file") MultipartFile multipartFile) {
		userAccountService.batchInsertWithoutAsyncWithoutObjectsplit(multipartFile);
		return ResponseEntity.ok("File Reading complete with async");
	}

	@PostMapping("/Async/split/object")
	public ResponseEntity<?> batchInsertWithoutAsyncWithObjectsplit(@RequestParam("file") MultipartFile multipartFile) {
		userAccountService.batchInsertWithoutAsyncWithObjectsplit(multipartFile);
		return ResponseEntity.ok("File Reading complete with async object split");
	}

}
