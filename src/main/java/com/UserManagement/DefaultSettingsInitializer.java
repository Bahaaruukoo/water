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
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//@Component
public class DefaultSettingsInitializer {

    private final SettingsRepository settingsRepository;
    private final PricingRepository pricingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;

    public DefaultSettingsInitializer(SettingsRepository settingsRepository,
                                      PricingRepository pricingRepository,
                                      UserService userService,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      RoleRepository roleRepository
                                      ) {
        this.settingsRepository = settingsRepository;
        this.pricingRepository = pricingRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }
    //@Bean
    public void initializeDefaultSettings() { //public ApplicationRunner initializeDefaultSettings() {
        //return args -> {
            if (settingsRepository.findAll().size() == 0) {
                Settings settings = new Settings();
                settingsRepository.save(settings);
            }
            if(pricingRepository.findAll().isEmpty()){
                List<Pricing> defaultPrices = new ArrayList<>();
                defaultPrices.add(new Pricing(0L,CustomerCategory.PRIVATE, 20.0, 21.0, 23.10, 26.10, 30.01, 36.01));
                defaultPrices.add(new Pricing(1L,CustomerCategory.BUSINESS, 22.05, 23.15, 27.96, 33.33, 39.68, 51.85));
                defaultPrices.add(new Pricing(2L,CustomerCategory.GOVERNMENT, 21.0, 22.05, 25.41, 29.50, 34.51, 43.21));
                defaultPrices.add(new Pricing(3L,CustomerCategory.PUBLIC, 21.0, 22.05, 25.41, 29.50, 34.51, 43.21));
                defaultPrices.add(new Pricing(4L,CustomerCategory.INDUSTRY, 22.05, 23.15, 27.96, 33.33, 39.68, 51.85));

                pricingRepository.saveAll(defaultPrices);
            }
            //Populate Role table with roles
            if(roleRepository.findAll().size() == 0){
                List<Role> roles = new ArrayList<>();

                Role roleAdmin = new Role();
                roleAdmin.setName("ROLE_ADMIN");
                roles.add(roleAdmin);

                Role roleReader = new Role();
                roleAdmin.setName("ROLE_READER");
                roles.add(roleReader);

                Role roleCashier = new Role();
                roleAdmin.setName("ROLE_CASHIER");
                roles.add(roleCashier);

                Role roleUser = new Role();
                roleAdmin.setName("ROLE_USER");
                roles.add(roleUser);

                Role roleAuditor = new Role();
                roleAdmin.setName("ROLE_AUDITOR");
                roles.add(roleAuditor);

                Role roleManager = new Role();
                roleAdmin.setName("ROLE_MANAGER");
                roles.add(roleManager);

                roleRepository.saveAll(roles);
            }
            //set root admin account
            if(userService.findUserByEmail("rootadmin@tuma.com") == null){
                List<Role> allRoles = new ArrayList<>();
                allRoles.addAll(roleRepository.findAll());

                //createSysAdmin(allRoles);
            }
            // Add more default settings as needed
    }

    @Transactional
    private void createSysAdmin(List<Role> allRoles){

        User user = new User();

        user.setName("System Root Admin");
        user.setPhone("0000000000");
        user.setEmail("rootadmin@tuma.com");
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setAddress("System");
        user.setAge(0);
        user.setGender("System");

        user.setRoles(allRoles);

        userRepository.save(user);

    }
}