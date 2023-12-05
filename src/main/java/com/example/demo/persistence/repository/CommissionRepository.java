package com.example.demo.persistence.repository;

import com.example.demo.persistence.entity.Comission;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommissionRepository extends JpaRepository<Comission, String> {
    Comission findByCurrency (String currency);

    @Modifying
    @Transactional
    @Query("update Comission u set u.currencyFrom = :currencyFrom where u.currency = :currency")
    void changeComissionFrom(@Param("currency") String currency, double currencyFrom);

    @Modifying
    @Transactional
    @Query("update Comission u set u.currencyTo = :currencyTo where u.currency = :currency")
    void changeComissionTo(@Param("currency") String currency, double currencyTo);
}
