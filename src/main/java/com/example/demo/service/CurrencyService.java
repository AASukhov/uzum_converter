package com.example.demo.service;

import com.example.demo.exception.*;
import com.example.demo.persistence.dto.request.CommissionCorrection;
import com.example.demo.persistence.dto.request.ConversionInput;
import com.example.demo.persistence.dto.response.*;
import com.example.demo.persistence.entity.Account;
import com.example.demo.persistence.entity.Course;
import com.example.demo.persistence.repository.AccountRepository;
import com.example.demo.persistence.repository.CommissionRepository;
import com.example.demo.persistence.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CurrencyService {

    @Value("${secret.key}")
    private String secretKey;
    private String BASE_URL = "https://cbu.uz/ru/arkhiv-kursov-valyut/json";

    private CourseRepository courseRepository;
    private AccountRepository accountRepository;
    private CommissionRepository commissionRepository;

    public CurrencyService (CourseRepository courseRepository,
                            AccountRepository accountRepository,
                            CommissionRepository commissionRepository) {
        this.courseRepository = courseRepository;
        this.accountRepository = accountRepository;
        this.commissionRepository = commissionRepository;
    }

    public void getData(LocalDate date) throws ExtractionException {
        RestTemplate template = new RestTemplate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        String link = BASE_URL + "/all/" + formatter.format(date) +"/";
        CurrencyInformationFromCB[] res = template.exchange(link, HttpMethod.GET, new HttpEntity<>(headers),
                CurrencyInformationFromCB[].class).getBody();
        if (res != null) {
            for (CurrencyInformationFromCB currency : res) {
                if (!courseRepository.existsByDateAndCurrency(date, currency.getName())) {
                    courseRepository.save(Course.builder()
                            .currency(currency.getName())
                            .rate(currency.getRate()/currency.getNominal())
                            .date(date)
                            .build());
                }
            }
        }
        courseRepository.save(Course.builder()
                        .currency("UZS")
                        .rate(1)
                        .date(date)
                .build());
    }

    public RateInformation getPairRate(LocalDate date, String pair) throws ExtractionException {
        String from = pair.substring(0,3);
        String to = pair.substring(4,7);
        checkCurrency(from);
        checkCurrency(to);
        double rateFrom, rateTo;
        if (from.equals("UZS")) {
            rateFrom = 1;
        } else {
            CurrencyInformationFromCB currInformationFrom = getInformationFromCB(date, from);
            rateFrom = currInformationFrom.getRate()/currInformationFrom.getNominal();
        }
        if (to.equals("UZS")) {
            rateTo = 1;
        } else {
            CurrencyInformationFromCB currInformationTo = getInformationFromCB(date, to);
            rateTo = currInformationTo.getRate()/currInformationTo.getNominal();
        }
        return new RateInformation(pair, rateFrom/rateTo);
    }

    public CurrencyInformationFromCB getInformationFromCB(LocalDate date, String currency) {
        RestTemplate template = new RestTemplate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        String link = BASE_URL + "/" + currency + "/" + formatter.format(date) +"/";
        CurrencyInformationFromCB[] result = template.exchange(link, HttpMethod.GET, new HttpEntity<>(headers),
                CurrencyInformationFromCB[].class).getBody();
        if (result != null) {
            return result[0];
        } else {
            throw new CurrencyNotFoundException("Currency not found");
        }
    }

    public AccountInformation getAccountInformation(String currency) {
        Account account = accountRepository.findByCurrency(currency)
                .orElseThrow(()-> new CurrencyNotFoundException("Currency not found"));
        return AccountInformation.builder()
                .currency(account.getCurrency())
                .amount(String.format(Locale.ENGLISH, "%,.6f", account.getAmount()))
                .build();
    }

    public List<AccountInformation> getAllAccountsInformation() {
        List<AccountInformation> result = new ArrayList<>();
        accountRepository.findAll().forEach(information ->
                result.add(AccountInformation.builder()
                        .currency(information.getCurrency())
                        .amount(String.format(Locale.ENGLISH, "%,.6f", information.getAmount()))
                        .build()));
        return result;
    }

    public ConversionInformation checkConversionInformation(String from, String to, double amount) {
        checkCurrency(from);
        checkCurrency(to);
        return new ConversionInformation(from + "/" + to, amount,
                String.format(Locale.ENGLISH, "%,.6f", calculateConversion(from, to, amount)));
    }

    public ConversionResult makeConversion(ConversionInput input) {
        String from = input.from(), to = input.to();
        double amount = input.amount();
        Account fromAccount = accountRepository.findByCurrency(from)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));
        Account toAccount = accountRepository.findByCurrency(to)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));
        if (fromAccount.getAmount() < amount) {
            throw new InsufficientFundsException("Not enough money on the account to convert");
        } else {
            double conversionResult = calculateConversion(from,to,amount);
            accountRepository.save(Account.builder()
                    .currency(from)
                    .amount(fromAccount.getAmount() - amount)
                    .build());
            accountRepository.save(Account.builder()
                            .currency(to)
                            .amount(toAccount.getAmount() + conversionResult)
                    .build());
            return new ConversionResult(to, String.format(Locale.ENGLISH, "%,.6f", conversionResult),
                    "Successfully converted");
        }
    }

    public double calculateConversion(String from, String to, double amount) {
        double commissionFrom = commissionRepository.findByCurrency(from).getCurrencyFrom();
        double courseFrom = courseRepository.findByCurrency(from).getRate();
        double commissionTo = commissionRepository.findByCurrency(to).getCurrencyTo();
        double courseTo = courseRepository.findByCurrency(to).getRate();
        return conversionCalculationManagement(amount, courseFrom, courseTo, commissionFrom, commissionTo);
    }

    public double conversionCalculationManagement(double inputAmount, double courseFrom, double courseTo,
                                                  double commissionFrom, double commissionTo) {
        return inputAmount * (courseFrom/courseTo * (1 - (commissionFrom + commissionTo)/100));
    }

    public MessageResponse setCommission(CommissionCorrection commissionCorrection, String token) {
        checkAccess(token);
        String pair = commissionCorrection.pair();
        String from = pair.substring(0,3);
        checkCurrency(from);
        String to = pair.substring(4,7);
        checkCurrency(to);
        checkCommission(commissionCorrection.comission());
        if (from.equals("UZS")) {
            if (!to.equals("UZS")) {
                commissionRepository.changeComissionTo(to, commissionCorrection.comission());
                return new MessageResponse("Commission was successfully updated");
            }
        } else {
            if (to.equals("UZS")) {
                commissionRepository.changeComissionFrom(from, commissionCorrection.comission());
                return new MessageResponse("Commission was successfully updated");
            }
        }
        throw new WrongCommissionException("Pair not found");
    }

    public List<CommissionInformation> getAllCommissions() {
        List<CommissionInformation> result = new ArrayList<>();
        commissionRepository.findAll().forEach(commission ->
            result.add(new CommissionInformation(commission.getCurrency(),
                    commission.getCurrencyFrom(),
                    commission.getCurrencyTo())));
        return result;
    }

    public List<CourseInformation> getAllCourses() {
        List<CourseInformation> result = new ArrayList<>();
        courseRepository.findAll().forEach(course ->
                result.add(new CourseInformation(course.getCurrency(),
                        String.format(Locale.ENGLISH, "%,.6f",course.getRate()),
                        course.getDate())));
        return result;
    }


    private void checkCurrency(String currency) {
        if (!courseRepository.existsByCurrency(currency) && !currency.equals("UZS")) {
            throw new CurrencyNotFoundException("Currency " + currency + " not found");
        }
    }

    private void checkAccess(String token) {
        if (!token.equals(secretKey)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private void checkCommission(double commissionRate) {
        if (commissionRate < 0 || commissionRate > 100) {
            throw new WrongCommissionException("Incorrect commission rate");
        }
    }
}
