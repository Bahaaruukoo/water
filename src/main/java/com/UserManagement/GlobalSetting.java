package com.UserManagement;

import com.UserManagement.account.Entity.Role;
import com.UserManagement.account.Entity.User;
import com.UserManagement.account.Repository.RoleRepository;
import com.UserManagement.account.Repository.UserRepository;
import com.UserManagement.account.service.UserService;
import com.UserManagement.billsystem.entities.Pricing;
import com.UserManagement.billsystem.entities.Settings;
import com.UserManagement.billsystem.repositories.PricingRepository;
import com.UserManagement.billsystem.repositories.SettingsRepository;
import com.UserManagement.enums.CustomerCategory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GlobalSetting implements ApplicationRunner {

    public static Settings globalSettings;
    public static Map<CustomerCategory, Pricing> globalPrices = new HashMap<>();

    @Autowired
    SettingsRepository settingsRepository;

    @Autowired
    PricingRepository pricingRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        DefaultSettingsInitializer defaultSettingsInitializer = new DefaultSettingsInitializer(
                settingsRepository, pricingRepository, userService, userRepository, passwordEncoder, roleRepository);
        defaultSettingsInitializer.initializeDefaultSettings();


        globalSettings = settingsRepository.findAll().get(0);
        for(Pricing pricing: pricingRepository.findAll()){
            globalPrices.put(pricing.getCustomerCategory(), pricing);
        }
    }
}
