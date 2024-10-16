package com.UserManagement.billsystem.services;

import com.UserManagement.billsystem.entities.Meter;
import com.UserManagement.billsystem.entities.MeterReading;
import com.UserManagement.billsystem.repositories.MeterRepository;
import com.UserManagement.enums.BillGenerationStatus;
import com.UserManagement.enums.MeterStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MeterService {

    @Autowired
    private MeterRepository meterRepository;
    @Autowired
    private MeterReadingService meterReadingService;

    public List<Meter> getAllMeters() {
        return meterRepository.findAll();
    }

    public Meter getMeterById(Long id) {
        return meterRepository.findById(id).orElse(null);
    }
    public Page<Meter> getPaginatedMeters(Pageable pageable) {
        return meterRepository.findAll(pageable);
    }

    @Transactional
    public Meter saveMeter(Meter meter) {
        double initialReading = meter.getInitialReading();
        //add initial meter reading to a meter reading table when installed to a site
        MeterReading meterReading = new MeterReading();
        meterReading.setMeterValue(initialReading);
        meterReading.setBillGenerationStatus(BillGenerationStatus.INIT);

        Meter meter1 = meterRepository.save(meter);
        meterReading.setMeter(meter1);
        meterReadingService.saveInitialReading(meterReading);

        return meter1;
    }

    public void deleteMeter(Long id) {
        meterRepository.deleteById(id);
    }

    public List<Meter> findAllMeters() {
        return meterRepository.findAll();
    }

    public long countTotalMeters() {
        return meterRepository.count();
    }

    public List<Meter> findAllMetersINSTACK() {
        return meterRepository.findByStatus(MeterStatus.INSTACK);
    }

    public List<Meter> findAllMetersINSTALLED() {
        //return meterRepository.findByStatus(MeterStatus.INSTALLED);
        return meterRepository.findMetersByStatusAndCustomerNotNull(MeterStatus.INSTALLED);
    }

    public Meter getMeterByMeterNumber(String meterId) {
        return meterRepository.findByMeterNumber(meterId).get();
    }

    public void update(Meter meter) {
        Meter meter1 = meterRepository.findById(meter.getId()).get();
        meter1.setStatus(meter.getStatus());
        meter1.setMeterSize(meter.getMeterSize());
        meter1.setInitialReading(meter.getInitialReading());
        meter1.setBrand(meter.getBrand());
        meter1.setModel(meter.getModel());
        meter1.setMeterColor(meter.getMeterColor());

        meterRepository.save(meter1);
    }

    public Page<Meter> findByMeterNumber(String meterNumber, Pageable pageable) {
        return meterRepository.findByMeterNumberContainingIgnoreCase(meterNumber, pageable);
    }
    public boolean getByMeterNumber(String meterNumber){
        return meterRepository.findByMeterNumber(meterNumber).isPresent();
    }
}
