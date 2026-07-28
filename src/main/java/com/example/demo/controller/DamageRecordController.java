package com.example.demo.controller;

import com.example.demo.dto.CreateDamageRecordRequest;
import com.example.demo.entity.DamageRecord;
import com.example.demo.service.DamageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;







@RestController
@RequestMapping("/damage-records")
public class DamageRecordController {

    private final DamageRecordService damageRecordService;

    @Autowired
    public DamageRecordController(DamageRecordService damageRecordService) {
        this.damageRecordService = damageRecordService;
    }

    //GET /damage-records tüm hasar kayıtlarını listeler
    @GetMapping
    public List<DamageRecord> getAllDamageRecords() {
        return damageRecordService.getAllDamageRecords();
    }

    //GET /damage-records/5 id'ye göre tek hasar kaydı getirir
    @GetMapping("/{id}")
    public DamageRecord getDamageRecordById(@PathVariable Long id) {
        return damageRecordService.getDamageRecordById(id);
    }

    //GET /damage-records/vehicle/3 belirli bir araca ait tüm hasar geçmişini getirir
    @GetMapping("/vehicle/{vehicleId}")
    public List<DamageRecord> getDamageRecordsByVehicleId(@PathVariable Long vehicleId) {
        return damageRecordService.getDamageRecordsByVehicleId(vehicleId);
    }

    // POST /damage-records yeni hasar kaydı oluşturur
    // Body'de CreateDamageRecordRequest formatında JSON bekliyoruz
    @PostMapping
    public DamageRecord createDamageRecord(@Valid @RequestBody CreateDamageRecordRequest request) {
        //service'in beklediği DamageRecord nesnesini, DTO'dan gelen bilgilerle oluşturuyoruz
        DamageRecord newRecord = new DamageRecord();
        newRecord.setDescription(request.getDescription());
        newRecord.setSeverity(request.getSeverity());
        newRecord.setEstimatedRepairCost(request.getEstimatedRepairCost());
        newRecord.setDamageDate(request.getDamageDate());

        return damageRecordService.createDamageRecord(
                request.getVehicleId(),
                request.getRentalId(),
                newRecord
        );
    }

    //PUT /damage-records/5/repair → hasarı onarıldı olarak işaretler
    //"returnVehicle" ile aynı mantık: bu da bir eylem (action), sıradan bir güncelleme değil
    @PutMapping("/{id}/repair")
    public DamageRecord markAsRepaired(@Valid @PathVariable Long id) {
        return damageRecordService.markAsRepaired(id);
    }

    //DELETE /damage-records/5 → hasar kaydını siler
    @DeleteMapping("/{id}")
    public void deleteDamageRecord(@PathVariable Long id) {
        damageRecordService.deleteDamageRecord(id);
    }
}