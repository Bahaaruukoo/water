package com.UserManagement.billsystem.services;

import com.UserManagement.GlobalSetting;
import com.UserManagement.billsystem.entities.Pricing;
import com.UserManagement.billsystem.repositories.PricingRepository;
import com.UserManagement.billsystem.repositories.SettingsRepository;
import com.UserManagement.enums.CustomerCategory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PricingService {

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    SettingsRepository settingsRepository;

    public List<Pricing> findAll() {
        return pricingRepository.findAll();
    }

    public Pricing findById(Long id) {
        return pricingRepository.findById(id).orElse(null);
    }

    public void save(Pricing pricing) {
        pricingRepository.save(pricing);
    }

    public void deleteById(Long id) {
        pricingRepository.deleteById(id);
    }

    public void updateGlobalPricing(){
        GlobalSetting.globalSettings = settingsRepository.findAll().get(0);
        for(Pricing pricing: pricingRepository.findAll()){
            GlobalSetting.globalPrices.put(pricing.getCustomerCategory(), pricing);
        }
    }

    @PostConstruct
    public void intiPrices(){
        if(pricingRepository.findAll().isEmpty()){
            List<Pricing> defaultPrices = new ArrayList<>();
            defaultPrices.add(new Pricing(0L, CustomerCategory.PRIVATE, 20.0, 21.0, 23.10, 26.10, 30.01, 36.01));
            defaultPrices.add(new Pricing(1L,CustomerCategory.BUSINESS, 22.05, 23.15, 27.96, 33.33, 39.68, 51.85));
            defaultPrices.add(new Pricing(2L,CustomerCategory.GOVERNMENT, 21.0, 22.05, 25.41, 29.50, 34.51, 43.21));
            defaultPrices.add(new Pricing(3L,CustomerCategory.GOVERNMENT, 21.0, 22.05, 25.41, 29.50, 34.51, 43.21));
            defaultPrices.add(new Pricing(4L,CustomerCategory.INDUSTRY, 22.05, 23.15, 27.96, 33.33, 39.68, 51.85));

            pricingRepository.saveAll(defaultPrices);
        }
    }
}
