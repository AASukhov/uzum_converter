package com.example.demo;

import com.example.demo.service.CurrencyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversionTest {

    @InjectMocks
    private CurrencyService service;

    @Test
    public void checkConversionManagementFunction() {
        double result = service.conversionCalculationManagement(1000, 12272, 1, 0, 0);
        Assertions.assertEquals(result, 12272000);
    }
}
