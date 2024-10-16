package com.UserManagement.enums;

public enum BillGenerationStatus {
    INIT,  // when first introduced to the system. Or be ready to deploy
    CAPTURED, // when meter is read by a reader . data captured
    GENERATED, // when bill is generated to the data captured
    VOIDED, // when the bill is voided
    EDITED
}
