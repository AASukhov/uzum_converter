package com.example.demo.persistence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConversionInformation {
    private String pair;
    private double currencyFrom;
    private String currencyTo;
}
