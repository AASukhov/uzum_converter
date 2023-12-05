package com.example.demo.persistence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConversionResult {
    private String currency;
    private String resultAmount;
    private String status;
}
