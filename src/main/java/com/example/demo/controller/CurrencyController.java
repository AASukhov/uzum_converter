package com.example.demo.controller;

import com.example.demo.persistence.dto.request.CommissionCorrection;
import com.example.demo.persistence.dto.request.ConversionInput;
import com.example.demo.persistence.dto.response.*;
import com.example.demo.service.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
public class CurrencyController {

    private CurrencyService service;

    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping("/officialrates")
    public ResponseEntity<RateInformation> getOfficialRates(@RequestParam("date") LocalDate date,
                                                            @RequestParam("pair") String pair) {
        return new ResponseEntity<>(service.getPairRate(date, pair), HttpStatus.OK);
    }

    @GetMapping("/convert")
    public ResponseEntity<ConversionInformation> conversionCheck(@RequestParam("from") String from,
                                                                 @RequestParam("to") String to,
                                                                 @RequestParam("amount") double amount) {
        return new ResponseEntity<>(service.checkConversionInformation(from, to, amount), HttpStatus.OK);
    }

    @PostMapping("/convert")
    public ResponseEntity<ConversionResult> conversionMake(@RequestBody ConversionInput input) {
        return new ResponseEntity<>(service.makeConversion(input), HttpStatus.OK);
    }

    @PostMapping("/setcomission")
    public ResponseEntity<MessageResponse> setCommission(@RequestBody CommissionCorrection correction,
                                                         @RequestHeader("security-token") String token) {
        return new ResponseEntity<>(service.setCommission(correction, token), HttpStatus.OK);
    }

    @GetMapping("/information")
    public ResponseEntity<AccountInformation> getAccountInformation(@RequestParam("currency") String currency) {
        return new ResponseEntity<>(service.getAccountInformation(currency), HttpStatus.OK);
    }

    @GetMapping("/information/all")
    public ResponseEntity<List<AccountInformation>> getAllAccountsInformation() {
        return new ResponseEntity<>(service.getAllAccountsInformation(), HttpStatus.OK);
    }

    @GetMapping("/courses/all")
    public ResponseEntity<List<CourseInformation>> getAllCoursesCurrentInformation() {
        return new ResponseEntity<>(service.getAllCourses(), HttpStatus.OK);
    }

    @GetMapping("/commissions/all")
    public ResponseEntity<List<CommissionInformation>> getAllCommissionsInformation() {
        return new ResponseEntity<>(service.getAllCommissions(), HttpStatus.OK);
    }

}