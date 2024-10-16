package com.UserManagement.billsystem.controllers;

import com.UserManagement.account.Entity.User;
import com.UserManagement.account.service.UserService;
import com.UserManagement.billsystem.dto.ReportForm;
import com.UserManagement.billsystem.entities.Bill;
import com.UserManagement.billsystem.entities.SoldBillReport;
import com.UserManagement.billsystem.repositories.BillRepository;
import com.UserManagement.billsystem.services.SoldBillReportService;
import com.UserManagement.enums.AuditStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class SoldBillReportController {

    @Autowired
    private SoldBillReportService reportService;
    @Autowired
    private UserService userService;

    @Autowired
    private BillRepository billRepository;



    @GetMapping("/list")
    public String filterReports(
            @RequestParam(required = false) AuditStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Double minCollection,
            @RequestParam(required = false) Double maxCollection,
            Principal principal,
            Model model) {

        User user = userService.findUserByEmail(principal.getName());

        if (status == null) status = AuditStatus.SUBMITTED;
        if (status == AuditStatus.PENDING) status = null;
        // Build a filtering logic in  service
        List<SoldBillReport> filteredReports = reportService.filterReportsForAUserLogedIn(status, endDate, minCollection, maxCollection, user);
        model.addAttribute("reports", filteredReports);
        model.addAttribute("content", "audit-report/report-list-for-user");
        return "admins";
        //return "audit-report/report-list";
    }

    @GetMapping("/view/{id}")
    public String viewReport(@PathVariable Long id, Model model) {
        SoldBillReport report = reportService.findById(id);
//        List<Bill> biils = billRepository.findByReport(report);

        model.addAttribute("report", report);
        model.addAttribute("auditStatus", AuditStatus.values());
        model.addAttribute("content", "audit-report/report-view");
        return "admins";
    }

    @GetMapping("/allsold/create")
    public String showCreateReportFormforAllUnauditedBills(Model model) {
        //model.addAttribute("reportForm", new ReportForm());  // Create a form object for user input
        model.addAttribute("content", "audit-report/create-unaudited-bills-report");
        return "admins";
    }
    @PostMapping("/allsold/create")
    public String createReportforAllUnauditedBills(Model model) {
        //model.addAttribute("reportForm", new ReportForm());  // Create a form object for user input
        User cashier = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        /*
        if (result.hasErrors()) {
            return "audit-report/create-report"; // Return to form if validation fails
        }
        */
        reportService.createReportForBillsSoldSoFar(cashier);
        return "redirect:/reports/list";
    }
    @GetMapping("/create")
    public String showCreateReportForm(Model model) {
        model.addAttribute("reportForm", new ReportForm());  // Create a form object for user input
        model.addAttribute("content", "audit-report/create-report");
        return "admins";
    }
    @PostMapping("/create")
    public String createReport(@ModelAttribute @Valid ReportForm form,  @AuthenticationPrincipal Principal principal) {
        //public String createReport(@ModelAttribute @Valid ReportForm form, BindingResult result, @AuthenticationPrincipal Principal principal) {
        //User cashier = userService.findUserByEmail(principal.getName());
        User cashier = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        /*
        if (result.hasErrors()) {
            return "audit-report/create-report"; // Return to form if validation fails
        }
        */
        reportService.createReport(cashier, form.getStartDate(), form.getEndDate());
        return "redirect:/reports/list";

    }

    @GetMapping("/audit-list")
    public String listReportsForAuditor( @RequestParam(required = false) AuditStatus status,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                         @RequestParam(required = false) Double minCollection,
                                         @RequestParam(required = false) Double maxCollection,

                                         Model model) {

        if (status == null) status = AuditStatus.SUBMITTED;
        if (status == AuditStatus.PENDING) status = null;
        // Build a filtering logic in your service
        List<SoldBillReport> filteredReports = reportService.filterReports(status, endDate, minCollection, maxCollection);
        model.addAttribute("reports", filteredReports);
        model.addAttribute("content", "audit-report/report-list-for-auditor");
        return "admins";
        //return "audit-report/report-list";
    }
        @GetMapping("/audit/{id}")
        public String auditReport(Model model, @PathVariable Long id, @AuthenticationPrincipal Principal principal) {
            //User auditor = userService.findUserByEmail(principal.getName());
            User auditor = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

            model.addAttribute("report",reportService.findReport(id, auditor));
            model.addAttribute("content", "audit-report/report-view-for-auditor");
            return "admins";
        }

        @PostMapping("/audit/{id}")
        public String auditReport(@PathVariable Long id,
                              @RequestParam("action") String action,
                              @AuthenticationPrincipal User auditorX, Model model) {
        User auditor = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if ("AUDIT".equals(action)) {
            model.addAttribute("report", reportService.approveReport(id, auditor));
            //reportService.approveReport(id, auditor);
        } else if ("REJECT".equals(action)) {
            model.addAttribute("report", reportService.rejectReport(id, auditor));
            //reportService.rejectReport(id, auditor);
        }
        model.addAttribute("content", "audit-report/report-view-for-auditor");//audit-report");
        return "admins";
        //return "audit-report/report-view";//""redirect:/reports/list";
        //return "redirect:/reports/list";
    }
        @GetMapping("/reject/{id}")
        public String rejectReport(@PathVariable Long id, @AuthenticationPrincipal User auditorX) {
            User auditor = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

            reportService.rejectReport(id, auditor);
            return "redirect:/reports/list";
        }






}

