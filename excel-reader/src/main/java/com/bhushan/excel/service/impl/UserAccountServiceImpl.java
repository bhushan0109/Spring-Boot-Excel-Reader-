package com.bhushan.excel.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.dhatim.fastexcel.reader.Row;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bhushan.excel.entity.UserAccount;
import com.bhushan.excel.repo.UserAccountRepository;
import com.bhushan.excel.service.UserAccountService;
import com.bhushan.excel.util.ExcelReader;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserAccountServiceImpl implements UserAccountService {

	private final UserAccountRepository userAccountRepository;

	private final ExcelReader excelReader;

	private static final ExecutorService executor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

//	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	without async time for 
//	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public void batchInsertWithoutAsync(MultipartFile multipartFile) {
		System.out.println("batch insert start");
		try (InputStream inputStream = multipartFile.getInputStream()) {
			long start1 = System.currentTimeMillis();
			List<UserAccount> userAccounts = excelReader.read(inputStream, this::mapToUserAccount);
			long end1 = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.println(
					"Execution time for excel read is = " + formatter.format((end1 - start1) / 1000d) + " seconds");
			System.out.println("total user Accounts size=" + userAccounts.size());
			long start = System.currentTimeMillis();
			userAccountRepository.saveAll(userAccounts); // all save one time
			// executeBatch(userAccounts);
			System.out.println("execute Batch done");
			long end = System.currentTimeMillis();
			NumberFormat formatter1 = new DecimalFormat("#0.00000");
			System.out.println(
					"Execution time is for all save data=" + formatter1.format((end - start) / 1000d) + " seconds");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	without async time for 
//	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public void batchInsertWithoutAsyncWithoutObjectsplit(MultipartFile multipartFile) {
		System.out.println("batch insert start");
		try (InputStream inputStream = multipartFile.getInputStream()) {
			long start1 = System.currentTimeMillis();
			List<UserAccount> userAccounts = excelReader.read(inputStream, this::mapToUserAccount);
			long end1 = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.println(
					"Execution time for excel read is = " + formatter.format((end1 - start1) / 1000d) + " seconds");
			System.out.println("total user Accounts size=" + userAccounts.size());
			long start = System.currentTimeMillis();
			// all save one time
			executeBatch(userAccounts);
			System.out.println("execute Batch done");
			long end = System.currentTimeMillis();
			NumberFormat formatter1 = new DecimalFormat("#0.00000");
			System.out.println(
					"Execution time is for all save data=" + formatter1.format((end - start) / 1000d) + " seconds");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	with async  time for object split
//	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public void batchInsertWithoutAsyncWithObjectsplit(MultipartFile multipartFile) {
		System.out.println("batch insert start");
		try (InputStream inputStream = multipartFile.getInputStream()) {
			long start1 = System.currentTimeMillis();
			List<UserAccount> userAccounts = excelReader.read(inputStream, this::mapToUserAccount);
			long end1 = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.println(
					"Execution time for excel read is = " + formatter.format((end1 - start1) / 1000d) + " seconds");
			System.out.println("total user Accounts size=" + userAccounts.size());
			long start = System.currentTimeMillis();
			int splitSize = 25;
			List<List<UserAccount>> partitions = splitUserAccounts(userAccounts, splitSize);
			System.out.println("sub partitions=" + partitions.size());
			CompletableFuture[] futures = new CompletableFuture[partitions.size()];
			for (int i = 0; i < partitions.size(); i++) {
				System.out.println("executeBatch=" + i);
				futures[i] = executeBatch(partitions.get(i));
		}
			System.out.println("execute Batch done");
			long end = System.currentTimeMillis();
			NumberFormat formatter1 = new DecimalFormat("#0.00000");
			System.out.println(
					"Execution time is for all save data=" + formatter1.format((end - start) / 1000d) + " seconds");
			// CompletableFuture.allOf(futures);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private CompletableFuture<Void> executeBatch(List<UserAccount> userAccounts) {
		return CompletableFuture.runAsync(() -> {
			System.out.println("Current Thread Name: " + Thread.currentThread().getName());
			userAccountRepository.saveAll(userAccounts).clear();
		}, executor);
	}

	private UserAccount mapToUserAccount(Row row) {
		return UserAccount.builder().firstName(excelReader.getStringValue(row, 0))
				.lastName(excelReader.getStringValue(row, 1)).email(excelReader.getStringValue(row, 2))
				.gender(excelReader.getStringValue(row, 3)).jobTitle(excelReader.getStringValue(row, 4)).build();
	}

	private List<List<UserAccount>> splitUserAccounts(List<UserAccount> userAccounts, int batchSize) {
		List<List<UserAccount>> batchUserAccounts = new ArrayList<>();
		int total = userAccounts.size() / batchSize;

		for (int i = 0; i < userAccounts.size(); i += total) {
			batchUserAccounts.add(userAccounts.subList(i, Math.min(i + total, userAccounts.size())));
		}
		return batchUserAccounts;
	}
}
