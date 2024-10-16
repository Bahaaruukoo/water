package com.UserManagement.billsystem.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class BillAuditController {

    @Autowired
    private BillAuditService billAuditService;

    @GetMapping("/billing/audit/{billId}")
    public String getBillAuditHistory(@PathVariable Long billId, Model model) {
        // Retrieve audit history
        List<Object[]> auditHistory = billAuditService.getBillAuditHistory(billId);

        // Add audit history to the model
        model.addAttribute("auditHistory", auditHistory);
        model.addAttribute("billId", billId);

        // Return the name of the Thymeleaf template
        return "audit/bill-audit-history";
    }
}
