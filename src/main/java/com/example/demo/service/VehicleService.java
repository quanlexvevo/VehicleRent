package com.example.demo.service;

// Vehicle entity'sini ve status enum'unu kullanacağız
import com.example.demo.entity.Vehicle;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//Bu sınıfın bir service bean'i olduğunu Spring'e bildiriyoruz
@Service
public class VehicleService {

    //Servis,veritabanı işlemleri için repository'ye ihtiyaç duyuyor
    private final VehicleRepository vehicleRepository;

    //spring, bu constructor'ı görünce VehicleRepository'yi otomatik olarak enjekte ediyor
    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    //tüm araçları listeler
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    //belirli bir id'ye sahip aracı getirir
    //bulunamazsa hata fırlatır,uygulama çökmez devam eder
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    //yeni bir araç ekler
    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    //var olan bir aracı günceller
    public Vehicle updateVehicle(Long id, Vehicle updatedVehicle) {
        // Önce eski kaydı buluyoruz
        Vehicle existingVehicle = getVehicleById(id);

        //eski kaydın alanlarını, yeni gelen bilgilerle güncelliyoruz
        existingVehicle.setPlate(updatedVehicle.getPlate());
        existingVehicle.setBrand(updatedVehicle.getBrand());
        existingVehicle.setModel(updatedVehicle.getModel());
        existingVehicle.setYear(updatedVehicle.getYear());
        existingVehicle.setColor(updatedVehicle.getColor());
        existingVehicle.setDailyRate(updatedVehicle.getDailyRate());
        existingVehicle.setStatus(updatedVehicle.getStatus());

        //güncellenmiş nesneyi veritabanına kaydediyoruz
        return vehicleRepository.save(existingVehicle);
    }

    //bir aracı siler
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}