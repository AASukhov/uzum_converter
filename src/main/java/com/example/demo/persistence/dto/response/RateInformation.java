package com.example.demo.persistence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RateInformation {
    private String pair;
    private double rate;
}
