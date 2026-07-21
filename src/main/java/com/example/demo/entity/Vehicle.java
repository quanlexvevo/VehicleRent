package com.example.demo.entity;

import com.example.demo.entity.enums.VehicleStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;

@Entity // diyo ki bu bir veritabanı tablosu bu sınıf böyle demek
public class Vehicle {

    @Id//primary keyimiz
    @GeneratedValue(strategy = GenerationType.IDENTITY) // sen id leri elle atama otomatik atasın diye var
    private Long id;

    private String plate;
    private String brand;
    private String model;

    @Column(name = "manufacture_year") //h2 veritabanında year kelimesi sorun yarattı bu yüzden yazdık, rezerve edilmiş kelimeydi h2 bunu anahtar kelime olarak görüyodu sutun ismi olarak değil
    private int year;
    private String color;
    private double dailyRate;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status; // available, rented, in_maintenance, damaged (müsait - kiarada - bakımda - hasarlı)



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus  status) {
        this.status = status;
    }
}