package com.bhushan.excel.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bhushan.excel.entity.UserAccount;

@Repository
public interface UserAccountRepository extends  JpaRepository<UserAccount, Long> {
}
