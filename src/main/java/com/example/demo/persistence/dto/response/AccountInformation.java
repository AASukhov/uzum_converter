package com.example.demo.persistence.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountInformation {
    private String currency;
    private String amount;
}
