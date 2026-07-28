package com.example.demo.entity;

import com.example.demo.entity.enums.VehicleStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Entity // diyo ki bu bir veritabanı tablosu bu sınıf böyle demek
public class Vehicle {

    @Id//primary keyimiz
    @GeneratedValue(strategy = GenerationType.IDENTITY) // sen id leri elle atama otomatik atasın diye var
    private Long id;

    @NotBlank(message = "Plate cannot be empty")
    private String plate;
    @NotBlank(message = "Brand cannot be empty")
    private String brand;

    @NotBlank(message = "Model cannot be empty")
    private String model;

    @Min(value = 1980, message = "Year must be 1980 or later")
    @Max(value = 2030, message = "Year cannot be later than 2030")
    @Column(name = "manufacture_year") //h2 veritabanında year kelimesi sorun yarattı bu yüzden yazdık, rezerve edilmiş kelimeydi h2 bunu anahtar kelime olarak görüyodu sutun ismi olarak değil
    private int year;
    @NotBlank(message = "Color cannot be empty")
    private String color;
    @Min(value = 0, message = "Daily rate cannot be negative")
    private double dailyRate;

    @NotNull(message = "Status cannot be null")
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