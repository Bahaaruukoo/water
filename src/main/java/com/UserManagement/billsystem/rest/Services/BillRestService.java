package com.UserManagement.billsystem.rest.Services;

import com.UserManagement.billsystem.entities.Bill;
import com.UserManagement.billsystem.repositories.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillRestService {

    @Autowired
    private BillRepository billRepository;

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }
}

