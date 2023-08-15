package com.bhushan.excel.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserAccountService {

	void batchInsertWithoutAsync(MultipartFile multipartFile);

	void batchInsertWithoutAsyncWithoutObjectsplit(MultipartFile multipartFile);

	void batchInsertWithoutAsyncWithObjectsplit(MultipartFile multipartFile);
}
