package com.example.demo.entity;

import com.example.demo.entity.enums.DamageSeverity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class DamageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //hasar hangi araçta oldu, bi araçta birden fazla hasar kayıt olabilir
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    //hasar hangi kiralamada meydana geldi, yine aynı şekilde bir kiralamada da birden fazla olabilir
    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    private LocalDate damageDate;
    private String description;

    @Enumerated(EnumType.STRING)
    private DamageSeverity severity;

    private double estimatedRepairCost; //tahmini tamir ücreti
    private boolean repaired;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public LocalDate getDamageDate() {
        return damageDate;
    }

    public void setDamageDate(LocalDate damageDate) {
        this.damageDate = damageDate;
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

    public boolean isRepaired() {
        return repaired;
    }

    public void setRepaired(boolean repaired) {
        this.repaired = repaired;
    }
}