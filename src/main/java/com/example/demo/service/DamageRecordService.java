package com.example.demo.service;

import com.example.demo.entity.DamageRecord;
import com.example.demo.entity.Rental;
import com.example.demo.entity.Vehicle;
import com.example.demo.entity.enums.VehicleStatus;
import com.example.demo.repository.DamageRecordRepository;
import com.example.demo.repository.RentalRepository;
import com.example.demo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DamageRecordService {

    //bu servis 3 repository'ye ihtiyaç duyuyor:
    //hasar kaydı, araç ve kiralama bilgisiyle çalışıyor
    private final DamageRecordRepository damageRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public DamageRecordService(DamageRecordRepository damageRecordRepository,
                               VehicleRepository vehicleRepository,
                               RentalRepository rentalRepository) {
        this.damageRecordRepository = damageRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    //tüm hasar kayıtlarını listeler
    public List<DamageRecord> getAllDamageRecords() {
        return damageRecordRepository.findAll();
    }

    //Belirli bir araca ait tüm hasar geçmişini getirir
    //(mesela "bu araç şu ana kadar kaç kere hasar aldı" sorusuna cevap verebilir)
    public List<DamageRecord> getDamageRecordsByVehicleId(Long vehicleId) { //stream api kullandık mesela onceden bakmıstım
        return damageRecordRepository.findAll()
                .stream()//akısı baslatıyoruz
                .filter(record -> record.getVehicle().getId().equals(vehicleId)) //filtreliyoruz
                // yukardaki kod sadece istediğimiz idye sahip arabayı seçmemiz için gerekli filtreleme
                .toList(); // sonucu tekrar listeye çeviriyoruz
    }

    //tek bir hasar kaydını id'sine göre getirir
    public DamageRecord getDamageRecordById(Long id) {
        return damageRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Damage record not found with id: " + id));
    }

    //hasar kaydı oluşturma
    public DamageRecord createDamageRecord(Long vehicleId, Long rentalId, DamageRecord newRecord) {

        // İlgili aracı ve kiralama kaydını veritabanından buluyoruz
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + rentalId));

        //yeni hasar kaydına, bulduğumuz araç ve kiralama bilgilerini bağlıyoruz
        newRecord.setVehicle(vehicle);
        newRecord.setRental(rental);

        //eğer tarih belirtilmediyse bugünün tarihini varsayılan yapıyoruz
        if (newRecord.getDamageDate() == null) {
            newRecord.setDamageDate(LocalDate.now());
        }

        //henüz onarılmadığı için varsayılan olarak false (onarılmadı) veriyoruz
        newRecord.setRepaired(false);

        //hasar kaydı oluşunca aracın durumunu otomatik "DAMAGED" yapıyoruz
        //böylece bu araç yeni bir kiralamaya açılamaz (RentalService'teki kontrol sayesinde)
        vehicle.setStatus(VehicleStatus.DAMAGED);
        vehicleRepository.save(vehicle);

        return damageRecordRepository.save(newRecord);
    }

    //hasar kaydını onarıldı olarak düzeltme
    public DamageRecord markAsRepaired(Long damageRecordId) {

        //hasar kaydımızı buluyoruz
        DamageRecord record = getDamageRecordById(damageRecordId);

        //onarıldı olarak işaretliyoruz
        record.setRepaired(true);

        //aracımızı tekrardan müsaite alıyoruz
        Vehicle vehicle = record.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        return damageRecordRepository.save(record);
    }

    //hasar kaydını siler
    public void deleteDamageRecord(Long id) {
        damageRecordRepository.deleteById(id);
    }
}