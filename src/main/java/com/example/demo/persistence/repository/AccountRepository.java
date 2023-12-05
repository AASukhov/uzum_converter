package com.example.demo.persistence.repository;

import com.example.demo.persistence.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AccountRepository extends JpaRepository <Account, Long>{
    Optional<Account> findByCurrency(String currency);
}
