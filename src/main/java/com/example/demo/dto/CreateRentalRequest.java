package com.example.demo.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

//Data Transfer Object dto demek bunu yaptık cünkü createrental 4 parametre alıyor, postmanden json gönderirken bu 4 bilgiyi
//taşıcak yardımcı sınıf olarak kullanıyoruz

//bu sınıf, Postman'den POST /rentals isteği atarken
//gönderilecek JSON'un "şeklini" tanımlıyor.
//Rental entity'sini direkt kullanmıyoruz çünkü Rental, Vehicle/Customer NESNESİ bekliyor,
//ama Postman'den sadece ID göndermek istiyoruz (34 numaralı araç, 7 numaralı müşteri gibi)
public class CreateRentalRequest {

    @NotNull(message = "Vehicle id is required")
    private Long vehicleId;

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;


    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}