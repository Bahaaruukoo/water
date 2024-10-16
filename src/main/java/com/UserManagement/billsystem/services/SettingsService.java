package com.UserManagement.billsystem.services;

import com.UserManagement.GlobalSetting;
import com.UserManagement.billsystem.entities.Settings;

import com.UserManagement.billsystem.repositories.SettingsRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;


    public List<Settings> findAll() {
        return settingsRepository.findAll();
    }


    public Settings findById(Long id) {
        Optional<Settings> result = settingsRepository.findById(id);
        return result.orElse(null);  // Return the found setting or null if not found
    }


    public void save(Settings setting) {
        settingsRepository.save(setting);
    }


    public void delete(Long id) {
        settingsRepository.deleteById(id);
    }

    @Transactional
    public void factoryReset(Long id) {
        Settings settings = settingsRepository.findById(id).orElseThrow(() -> new RuntimeException("Setting not found"));
        settingsRepository.delete(settings);
        Settings settings1 = new Settings();
/*
        settings.setDueDateAfter(settings1.getDueDateAfter());
        settings.setLatePaymentFee(settings1.getLatePaymentFee());
        settings.setMeterRentalPrice(settings1.getMeterRentalPrice());
        settings.setRejectLessReading(settings1.isRejectLessReading());
*/
        settingsRepository.save(settings1);
    }

    @PostConstruct
    public void initializeSettings (){
        if (settingsRepository.findAll().toArray().length == 0) {
            Settings settings = new Settings();
            settingsRepository.save(settings);
        }
    }

    public void updateGlobalSettings() {
        GlobalSetting.globalSettings = settingsRepository.findAll().get(0);
    }

    public void setSchedules( String billGenerationFrequency) {
        if ( billGenerationFrequency == null  ){
            return;
        }
/*
        Map<String , String> salesReportFrequencyMap = new HashMap<>();
        salesReportFrequencyMap.put("DAILY", "Daily");
        salesReportFrequencyMap.put("WEEKLY_1", "Monday");
        salesReportFrequencyMap.put("WEEKLY_2", "Tuesday");
        salesReportFrequencyMap.put("WEEKLY_3", "Wednesday");
        salesReportFrequencyMap.put("WEEKLY_4", "Thursday");
        salesReportFrequencyMap.put("WEEKLY_5", "Friday");
        salesReportFrequencyMap.put("WEEKLY_6", "Saturday");

        settings.setReportGenerationTimeMinute(reportMinute);
        settings.setReportGenerationTimeHour(reportHour);
        settings.setReportGenerationFrequency(salesReportFrequencyMap.get(saleReportFrequency));
        settings.setBillGenerationFrequency(billGenerationFrequency);
*/
        Settings settings = settingsRepository.findAll().get(0);
        settings.setBillGenerationFrequency(billGenerationFrequency);

        settingsRepository.save(settings);
/*
        Map<String , String> reportHoureMap = new HashMap<>();
        reportHoureMap.put("1", "1");
        reportHoureMap.put("2", "1");
        reportHoureMap.put("3", "1");
        reportHoureMap.put("4", "1");
        reportHoureMap.put("5", "1");
        reportHoureMap.put("6", "1");
        reportHoureMap.put("7", "1");
        reportHoureMap.put("8", "1");
        reportHoureMap.put("9", "1");
        reportHoureMap.put("10", "1");
        reportHoureMap.put("11", "1");
        reportHoureMap.put("12", "1");

        Map<String , String> reportMinutMap = new HashMap<>();
        reportMinutMap.put("0", "0");
        reportMinutMap.put("15", "15");
        reportMinutMap.put("30", "30");
        reportMinutMap.put("45", "45");

        String generationFrequency = salesReportFrequencyMap.getOrDefault(saleReportFrequency, "*");
        String hourFrequency = reportHoureMap.getOrDefault(reportHour, "*");
        String minuteFrequency = reportMinutMap.getOrDefault(reportMinute, "*");
        String billFrequency = billGenerationFrequency;

        String salesSchedule = "0 " + minuteFrequency + " " + hourFrequency + " ";
        if (generationFrequency == "daily"){
            salesSchedule = salesSchedule + " * * *";
        }
        String billSchedule = "0 0 0 " + billFrequency + " * *";
        //"0 0 0 5,6 * *"
*/
    }
}
