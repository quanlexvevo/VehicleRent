package com.example.demo.repository;

import com.example.demo.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository den miras alındığı için otomatik olarak, findAll() , findById(id), save(vehicle) -> yeni araç ekle veya güncelle,
//deleteById(id) gibi fonksiyonlara erişimimiz otomatik oluyor



public interface VehicleRepository extends JpaRepository<Vehicle, Long> { // Vehicle entitysi için çalısıyor, vehicle idsinin tipi long
    boolean existsByPlateIgnoreCase(String plate);
}