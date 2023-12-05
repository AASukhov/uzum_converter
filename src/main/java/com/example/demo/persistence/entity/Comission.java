package com.example.demo.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comissions")
@AllArgsConstructor
@NoArgsConstructor
public class Comission {
    @Id
    private String currency;
    private double currencyFrom;
    private double currencyTo;
}
