package com.example.demo.persistence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommissionInformation {
    private String currency;
    private double currencyFrom;
    private double currencyTo;
}
