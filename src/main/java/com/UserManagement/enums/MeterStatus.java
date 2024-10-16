package com.UserManagement.enums;

public enum MeterStatus {
    INSTACK, // when added to inventory
    INSTALLED,  // when installed at a site or assigned to a customer
    RETIRED,  // decommissioned, not working anymore
    MAINTENANCE // under maintenance
}
