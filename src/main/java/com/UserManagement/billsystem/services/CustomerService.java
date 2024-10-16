package com.UserManagement.billsystem.services;

import com.UserManagement.billsystem.entities.Customer;
import com.UserManagement.billsystem.entities.Meter;
import com.UserManagement.billsystem.repositories.CustomerRepository;
import com.UserManagement.billsystem.repositories.MeterRepository;
import com.UserManagement.enums.MeterStatus;
import com.UserManagement.enums.ServiceStatus;
import com.UserManagement.exception.CustomerExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MeterRepository meterRepository;

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Page<Customer> getPaginatedCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Customer customer) {
        String phone = customer.getPhoneNumber();
        customer.setBillNumber(phone);
        Meter meter = customer.getMeter();
        customer.setRegistrationDate(LocalDate.now());
        if (meter.getStatus() != MeterStatus.INSTACK){
            throw new RuntimeException("This meter (" + meter.getMeterNumber() +
                    ") is not IN STOCK. First you have to change the status from " + meter.getStatus() + " to " +
                    MeterStatus.INSTACK);
        }
        meter.setStatus(MeterStatus.INSTALLED);

        customerRepository.save(customer);
        meterRepository.save(meter);
        /*
        try {
        } catch (Exception ex){
            throw new CustomerExistsException("Customer exists " + ex.getMessage());
        }
        */
    }

    public long countTotalCustomers() {
        return customerRepository.count();
    }

    public void update(Customer customer) {
        Customer customer1 = customerRepository.findById(customer.getId()).get();
        customer1.setName(customer.getName());
        customer1.setKebele(customer.getKebele());
        customer1.setHouseNumber(customer.getHouseNumber());
        customer1.setPhoneNumber(customer.getPhoneNumber());
        customer1.setCustomerCategory(customer.getCustomerCategory());
        customer1.setServiceStatus(customer.getServiceStatus());
        customer1.setOrdinantNumber(customer.getOrdinantNumber());

        customerRepository.save(customer1);
    }
/*
    public Page<Customer> getFilteredCustomers(String phoneNumber, String meterNumber, ServiceStatus status, Pageable pageable) {
        if ((phoneNumber != null && !phoneNumber.isEmpty()) ||
                (meterNumber != null && !meterNumber.isEmpty()) ||
                (status != null )) {

            // Apply filtering based on available fields
            return customerRepository.findFilteredCustomers(phoneNumber, meterNumber, status, pageable);
        } else {
            // If no filters applied, return all customers
            return customerRepository.findAll(pageable);
        }
    } */
    public Page<Customer> getFilteredCustomers(String phoneNumber, String meterNumber, ServiceStatus status, Pageable pageable) {
        // Your logic to filter customers based on phoneNumber, meterNumber, and status
        return customerRepository.findFilteredCustomers(phoneNumber, meterNumber, status, pageable);
    }

    public boolean findByPhone(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber).isPresent() || customerRepository.findByBillNumber(phoneNumber).isPresent();


    }
}
