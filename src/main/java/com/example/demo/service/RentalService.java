package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Rental;
import com.example.demo.entity.Vehicle;
import com.example.demo.entity.enums.RentalStatus;
import com.example.demo.entity.enums.VehicleStatus;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.RentalRepository;
import com.example.demo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {
    //3 repoya ihtiyacımız var, hem araç hem müşteri hem kiralama bilgisiyle işlem yapıyoruz
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    @Autowired // Spring, bu constructor'ı görünce 3 repository'yi de otomatik olarak bize veriyor (dependency injection)
    public RentalService(RentalRepository rentalRepository,
                         VehicleRepository vehicleRepository,
                         CustomerRepository customerRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }


    public List<Rental> getAllRentals() { //tüm kiralama kayıtları
        return rentalRepository.findAll();
    }

    public Rental getRentalById(Long id) { //belli bir id ye sahip kiralamaları getirir,eğer yoska hata fırlatır uygulama çökmez
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
    }

    //yeni kiralama oluşturma
    public Rental createRental(Long vehicleId, Long customerId, LocalDate startDate, LocalDate endDate) {

       //verilen id'lerle gerçek araç ve müşteri nesnelerini veritabanından buluyoruz
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));


        //eğer araç kiradaysa,bakımdaysa veyahut hasarlıysa izin vermicek
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available for rental");
        }

        //yeni bir kiralama listesi oluşturup bilgileri dolduruyoruz
        Rental rental = new Rental();
        rental.setVehicle(vehicle);
        rental.setCustomer(customer);
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        rental.setStatus(RentalStatus.ONGOING); //kiralama başlayıp devam ediceği için ongoing

        //burda ise tarih farkını alıp günlük ücretle çarpıp fiyat hesaplıyoruz
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        rental.setTotalPrice(days * vehicle.getDailyRate());

        //artık araç kirada bu yüzden aracın durumunu RENTED olarak güncellememiz lazım,
        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle); //bunu da db ye kaydediyoruz

        return rentalRepository.save(rental); //kiralama kayıtını veritabanına kaydedip geri döndürüyoruz
    }


    // aracı teslim alma
    public Rental returnVehicle(Long rentalId) {

       //kiralama kaydı bulunuyor
        Rental rental = getRentalById(rentalId);

        //gerçek teslim tarihi bugün olarak işaretleniyor
        rental.setActualReturnDate(LocalDate.now());
        rental.setStatus(RentalStatus.COMPLETED); //kiralamamız tamamlandı

        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE); // aracı tekrar müsait duruma geçiriyoruz
        vehicleRepository.save(vehicle); // bu değişikliği veritabanına kaydediyoruz

        return rentalRepository.save(rental); //güncellenmiş kiralama kaydını veritabanına kaydedip döndürüyoruz
    }

    //kiralama kaydı silme
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}