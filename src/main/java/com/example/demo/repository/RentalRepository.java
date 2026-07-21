package com.example.demo.repository;

import com.example.demo.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;



public interface RentalRepository extends JpaRepository<Rental, Long> {
}