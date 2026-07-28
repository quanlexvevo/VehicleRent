package com.example.demo.dto;

import com.example.demo.entity.enums.DamageSeverity;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

// Postman'den POST /damage-records isteği atarken
// gönderilecek JSON'un şeklini tanımlıyoruz
public class CreateDamageRecordRequest {

    @NotNull(message = "Vehicle id is required")
    private Long vehicleId;
    @NotNull(message = "Rental id is required")
    private Long rentalId;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    @NotNull(message = "Severity is required")
    private DamageSeverity severity;
    @Min(value = 0, message = "Estimated repair cost cannot be negative")
    private double estimatedRepairCost;
    private LocalDate damageDate; //opsiyonel,boş bırakılırsa Service otomatik bugünün tarihini atıyor

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DamageSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DamageSeverity severity) {
        this.severity = severity;
    }

    public double getEstimatedRepairCost() {
        return estimatedRepairCost;
    }

    public void setEstimatedRepairCost(double estimatedRepairCost) {
        this.estimatedRepairCost = estimatedRepairCost;
    }

    public LocalDate getDamageDate() {
        return damageDate;
    }

    public void setDamageDate(LocalDate damageDate) {
        this.damageDate = damageDate;
    }
}