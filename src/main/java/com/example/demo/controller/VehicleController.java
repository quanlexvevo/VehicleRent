package com.example.demo.controller;

import com.example.demo.entity.Vehicle;
import com.example.demo.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

// Bu sınıfın bir REST controller olduğunu, yani HTTP isteklerini karşılayacağını belirtir
// @RestController = @Controller + @ResponseBody (dönen veriyi otomatik JSON'a çevirir)
@RestController

//bu controller'daki tüm endpoint'lerin başına otomatik olarak "/vehicles" eklenir
//ornegin aşağıdaki @GetMapping("") aslında "/vehicles" demek olur
@RequestMapping("/vehicles")
public class VehicleController {

    //controller,Service'e ihtiyaç duyuyor
    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // GET /vehicles
    // Tüm araçları listeler
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    // GET /vehicles/5
    // @PathVariable ile URL'deki {id} kısmını yakalıyoruz (örn: 5)
    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    // POST /vehicles
    // Yeni araç eklemek için kullanılır
    // @RequestBody ile, Postman'den gönderilen JSON verisi otomatik olarak
    // bir Vehicle nesnesine çevrilir
    @PostMapping
    public Vehicle addVehicle(@Valid @RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    // PUT /vehicles/5
    // Var olan bir aracı günceller
    @PutMapping("/{id}")
    public Vehicle updateVehicle(@PathVariable Long id,@Valid @RequestBody Vehicle vehicle) {
        return vehicleService.updateVehicle(id, vehicle);
    }

    // DELETE /vehicles/5
    // Var olan bir aracı siler
    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }
}