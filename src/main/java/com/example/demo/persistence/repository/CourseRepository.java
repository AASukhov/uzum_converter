package com.example.demo.persistence.repository;

import com.example.demo.persistence.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByDateAndCurrency(LocalDate date, String currency);
    boolean existsByCurrency(String currency);
    Course findByCurrency(String currency);
}
