package com.example.demo.entity;

import com.example.demo.entity.enums.RentalStatus;
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
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ManyToOne birden çok kiralama olabilir demek,yani 1 araç farklı zamanlarda birden fazla kişiye kiralanabilir,
    //aynı şekilde bi müşteri de birden fazla araç kiralayabilir , bu ilşki tipine Many to one deniyor

    @ManyToOne
    @JoinColumn(name = "vehicle_id") // foreign key, bu ilişkinin hangi sutun adıyla saklanacağını söylüyor
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "customer_id") // aynı şekilde yine customer_id adında yeni sutun acılcak
    private Customer customer;

    private LocalDate startDate; //Planlanan kiralama tarihleri olarak kullanıcam, start - end
    private LocalDate endDate;
    private LocalDate actualReturnDate; // olası geç teslim durumları için de gerçekte ne zaman teslim edildiğini anlamak için bunu kullancam
    private double totalPrice;

    //bunu yazmasaydık enumda index numarasına göre kaydedicekti, şimdi enum ismiyle benim istediğim gibi kayıt edicek (rentalstatus)
    @Enumerated(EnumType.STRING)
    private RentalStatus status;



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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }
}