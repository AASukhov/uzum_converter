package com.example.demo.persistence.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyInformationFromCB {
    @JsonProperty("Code")
    private int code;
    @JsonProperty("Ccy")
    private String name;
    @JsonProperty("Nominal")
    private int nominal;
    @JsonProperty("Rate")
    private double rate;
}
