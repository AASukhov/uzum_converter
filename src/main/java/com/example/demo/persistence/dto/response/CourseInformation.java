package com.example.demo.persistence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CourseInformation {
    private String currency;
    private String rate;
    private LocalDate date;
}
