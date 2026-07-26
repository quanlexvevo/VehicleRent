package com.example.demo.controller;

import com.example.demo.dto.CreateRentalRequest;
import com.example.demo.entity.Rental;
import com.example.demo.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    //GET /rentals → tüm kiralama kayıtlarını listeler
    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalService.getAllRentals();
    }

    //GET /rentals/5 → id'ye göre tek kiralama kaydı getirir
    @GetMapping("/{id}")
    public Rental getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id);
    }

    //POST /rentals → yeni kiralama başlatır
    //body'de CreateRentalRequest formatında JSON bekliyoruz (vehicleId, customerId, startDate, endDate)
    @PostMapping
    public Rental createRental(@RequestBody CreateRentalRequest request) {
        return rentalService.createRental(
                request.getVehicleId(),
                request.getCustomerId(),
                request.getStartDate(),
                request.getEndDate()
        );
    }

    //PUT /rentals/5/return → aracı teslim alır, kiralamayı tamamlar
    //Ayrı bir URL parçası ("/return") ekledik çünkü bu normal bir "güncelleme" değil,
    //teslim alma işlemi,yani özel endpoint, peki normal puttan ne farkı var, json gondermeye gerek yok, sadece urlye id
    //gönderiyoruz rentalserivce'teki returnvehicle metodu bunu otomatik yapıyor,kontrol sistemde yani yanlıs girme
    //riskini azaltıyor
    @PutMapping("/{id}/return")
    public Rental returnVehicle(@PathVariable Long id) {
        return rentalService.returnVehicle(id);
    }

    //DELETE /rentals/5 → kiralama kaydını siler
    @DeleteMapping("/{id}")
    public void deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
    }
}