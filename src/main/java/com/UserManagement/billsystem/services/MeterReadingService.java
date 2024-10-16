package com.UserManagement.billsystem.services;

import com.UserManagement.GlobalSetting;
import com.UserManagement.account.Entity.User;
import com.UserManagement.account.Repository.UserRepository;
import com.UserManagement.billsystem.entities.Meter;
import com.UserManagement.billsystem.entities.MeterReading;
import com.UserManagement.billsystem.entities.Settings;
import com.UserManagement.billsystem.repositories.MeterReadingRepository;
import com.UserManagement.billsystem.repositories.MeterRepository;
import com.UserManagement.billsystem.repositories.SettingsRepository;
import com.UserManagement.billsystem.rest.dto.MeterReadingDto;
import com.UserManagement.billsystem.rest.dto.ReadingResponseDto;
import com.UserManagement.enums.BillGenerationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class MeterReadingService {

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    public List<MeterReading> findAll() {
        return meterReadingRepository.findAll();
    }

    public MeterReading findById(Long id) {
        return meterReadingRepository.findById(id).orElse(null);
    }

    public ReadingResponseDto save(MeterReading meterReading) {

        double prevReading;
        Settings settings = settingsRepository.findAll().get(0);
        meterReading.setReadingDate(LocalDateTime.now());// Automatically set the current date for reading

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(username);
        meterReading.setReader(currentUser); // Set the current logged-in user as the reader

        if (meterReading.getBillGenerationStatus() != BillGenerationStatus.INIT) {
            prevReading = meterReadingRepository.findPreviousReading(meterReading.getMeter().getId(), LocalDateTime.now()).getMeterValue();


            if (prevReading > meterReading.getMeterValue()) {
                if (!GlobalSetting.globalSettings.isRejectLessReading()) {
                    meterReading.setNegativeReading(true);
                } else {
                    throw new RuntimeException("Current reading cannot be lower than previous reading");
                }

            }
        }
        MeterReading mr = meterReadingRepository.save(meterReading);
        ReadingResponseDto rd = new ReadingResponseDto();
        rd.setMeterId(mr.getMeter().getMeterNumber());
        rd.setMReading(mr.getMeterValue());
        rd.setCustomerName( mr.getMeter().getCustomer().getName());
        rd.setReadingDatetime(mr.getReadingDate());

        return rd;
    }

    public MeterReading saveInitialReading(MeterReading meterReading) {

        double prevReading;
        Settings settings = settingsRepository.findAll().get(0);
        meterReading.setReadingDate(LocalDateTime.now());// Automatically set the current date for reading

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(username);
        meterReading.setReader(currentUser); // Set the current logged-in user as the reader

        MeterReading mr = meterReadingRepository.save(meterReading);

        return mr;
    }

    public void deleteById(Long id) {
        meterReadingRepository.deleteById(id);
    }

    public List<Meter> findAllMeters() {
        return meterRepository.findAll();
    }

    public MeterReading update(MeterReading meterReading) {
        if(meterReading.getBillGenerationStatus() != BillGenerationStatus.VOIDED){
            throw new RuntimeException("Cannot update a meter reading with status " + meterReading.getBillGenerationStatus());
        }
        meterReading.setBillGenerationStatus(BillGenerationStatus.EDITED);
        return meterReadingRepository.save(meterReading);
    }
    public Page<MeterReading> getPaginatedMeterReadings(Pageable pageable) {
        return meterReadingRepository.findAll(pageable);
    }
    public Page<MeterReading> findAllWithFilters(String meterNumber, LocalDate readingStartDate,
                                                 LocalDate readingEndDate, Boolean negativeReading,
                                                 Pageable pageable) {
        Specification<MeterReading> spec = Specification.where(null);

        if (meterNumber != null && !meterNumber.isEmpty()) {
            spec = spec.and(MeterReadingSpecifications.hasMeterNumber(meterNumber));
        }

        if (readingStartDate != null) {
            spec = spec.and(MeterReadingSpecifications.hasReadingDateAfter(readingStartDate));
        }

        if (readingEndDate != null) {
            spec = spec.and(MeterReadingSpecifications.hasReadingDateBefore(readingEndDate));
        }

        if (negativeReading != null) {
            spec = spec.and(MeterReadingSpecifications.hasNegativeReading(negativeReading));
        } else {
            spec = spec.and(MeterReadingSpecifications.hasNegativeReading(false));

        }

        return meterReadingRepository.findAll(spec, pageable);
    }

    public class MeterReadingSpecifications {

        public static Specification<MeterReading> hasMeterNumber(String meterNumber) {
            return (root, query, cb) -> cb.equal(root.get("meter").get("meterNumber"), meterNumber);
        }

        public static Specification<MeterReading> hasReadingDateAfter(LocalDate date) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("readingDate"), date.atStartOfDay());
        }

        public static Specification<MeterReading> hasReadingDateBefore(LocalDate date) {
            return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("readingDate"), date.atTime(LocalTime.MAX));
        }

        public static Specification<MeterReading> hasNegativeReading(boolean negative) {
            return (root, query, cb) -> cb.equal(root.get("negativeReading"), negative);
        }
    }


}