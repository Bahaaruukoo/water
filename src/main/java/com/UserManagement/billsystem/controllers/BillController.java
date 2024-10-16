package com.UserManagement.billsystem.controllers;

import com.UserManagement.account.Entity.User;
import com.UserManagement.account.service.UserService;
import com.UserManagement.billsystem.dto.KebeleCategoryBillReportDTO;
import com.UserManagement.billsystem.entities.Bill;
import com.UserManagement.billsystem.services.BillService;
import com.UserManagement.billsystem.services.PdfService;
import com.UserManagement.enums.AuditStatus;
import com.UserManagement.enums.BillPaymentStatus;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfAcroForm;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/billing")
public class BillController {
    @Autowired
    private BillService billService;

    @Autowired
    private UserService userService;

    @Autowired
    private PdfService pdfService;

    @GetMapping()
    public String getBills(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) String billNumber,
                           @RequestParam(required = false) String status) {

        if (page < 0) {
            page = 0; // Ensure page number is not negative
            System.out.println("page number less than zero");
        }

        // Create Pageable with sorting by invoiceDate descending
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "invoiceDate"));

        // Get the paginated bills, filtering by bill number and status if provided
        Page<Bill> billPage;
        if ((billNumber != null && !billNumber.isEmpty()) || (status != null && !status.isEmpty())) {
            billPage = billService.getFilteredBills(billNumber, status, pageable);
        } else {
            billPage = billService.getPaginatedBills(pageable);
        }

        model.addAttribute("billPage", billPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", billPage.getTotalPages());
        model.addAttribute("billNumber", billNumber);  // Keep the entered billNumber in the input field
        model.addAttribute("status", status);          // Keep the selected status in the dropdown
        model.addAttribute("content", "bills/bill-list"); // Reference to a Thymeleaf fragment

        return "admins";
    }


    @GetMapping("/void/{id}")
    public String voidBill(@PathVariable Long id) {
        billService.voidBill(id);
        return "redirect:/billing";
    }
    @GetMapping("/view/{id}")
    public String viewBillDetail(Model model, @PathVariable Long id) {
        Bill bill = billService.viewBillDetail(id);
        model.addAttribute("bill", bill);
        model.addAttribute("status", BillPaymentStatus.UNSOLD);
        model.addAttribute("content", "bills/bill-detail"); // Reference to a Thymeleaf fragment
        return "admins";
    }
    @GetMapping("/generate/{id}")
    public String generateBill(@PathVariable Long id) {
        billService.billRequest(id);
        return "redirect:/billing";
    }
    @GetMapping("/{id}/pay")
    public String collectPayment(@PathVariable Long id, @AuthenticationPrincipal Principal principal) {

        User cashier = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Bill bill = billService.findById(id);
        if (bill != null && BillPaymentStatus.UNSOLD.equals(bill.getStatus())) {
            bill.setStatus(BillPaymentStatus.SOLD);
            //bill.setAmountPaid(amountPaid);
            bill.setSoldBy(cashier);
            bill.setSoldDate(LocalDate.now());
            billService.save(bill);
        }
        return "redirect:/billing/view/" + id;
    }
    @GetMapping("/kebele-category-report")
    public String getKebeleCategoryReport(Model model) {
        List<KebeleCategoryBillReportDTO> report = billService.getGroupedBillReport();
        model.addAttribute("report", report);
        model.addAttribute("content", "audit-report/kebele-category-report"); // Reference to a Thymeleaf fragment

        return "admins";
    }
    @GetMapping("/reports/sold")
    public String getSoldBillReport(@RequestParam(name = "month", required = false) Integer month,
                                @RequestParam(name = "year", required = false) Integer year, Model model) {
        // Default to the current month if none is selected
        if (month == null) {
            //month = LocalDate.now().getMonthValue();
            month = 0;
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        // Fetch the list of sold bills for the selected month
        List<Bill> bills = billService.getSoldBillsByMonthAndYear(month, year);

        // Calculate totals
        double totalAmount = bills.stream().mapToDouble(Bill::getAmount).sum();
        double totalMeterRent = bills.stream().mapToDouble(Bill::getMeterRent).sum();
        double totalServiceCharge = bills.stream().mapToDouble(Bill::getServiceCharge).sum();
        double totalPenalty = bills.stream().mapToDouble(Bill::getPenalty).sum();
        double totalBill = bills.stream().mapToDouble(Bill::getTotalBill).sum();
        double totalConsumption = bills.stream().mapToDouble(Bill::getConsumption).sum();
        // Add data to model
        model.addAttribute("bills", bills);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalMeterRent", totalMeterRent);
        model.addAttribute("totalServiceCharge", totalServiceCharge);
        model.addAttribute("totalPenalty", totalPenalty);
        model.addAttribute("totalBill", totalBill);
        model.addAttribute("totalConsumption", totalConsumption);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);//IntStream.rangeClosed(2020, LocalDate.now().getYear())
        //.boxed().collect(Collectors.toList()));
        model.addAttribute("content", "bills/sold-bill-report"); // Reference to a Thymeleaf fragment

        return "admins";
    }

    @GetMapping("/reports/unsold")
    public String getUnsoldBillReport(@RequestParam(name = "month", required = false) Integer month,
                                @RequestParam(name = "year", required = false) Integer year, Model model) {
        // Default to the current month if none is selected
        if (month == null) {
            //month = LocalDate.now().getMonthValue();
            month = 0;
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        // Fetch the list of sold bills for the selected month
        List<Bill> bills = billService.getUnSoldBillsByMonthAndYear(month, year);

        // Calculate totals
        double totalConsumption = bills.stream().mapToDouble(Bill::getConsumption).sum();
        double totalAmount = bills.stream().mapToDouble(Bill::getAmount).sum();
        double totalMeterRent = bills.stream().mapToDouble(Bill::getMeterRent).sum();
        double totalServiceCharge = bills.stream().mapToDouble(Bill::getServiceCharge).sum();
        double totalPenalty = bills.stream().mapToDouble(Bill::getPenalty).sum();
        double totalBill = bills.stream().mapToDouble(Bill::getTotalBill).sum();
        // Add data to model
        model.addAttribute("bills", bills);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalMeterRent", totalMeterRent);
        model.addAttribute("totalServiceCharge", totalServiceCharge);
        model.addAttribute("totalPenalty", totalPenalty);
        model.addAttribute("totalBill", totalBill);
        model.addAttribute("totalConsumption", totalConsumption);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);//IntStream.rangeClosed(2020, LocalDate.now().getYear())
        //.boxed().collect(Collectors.toList()));
        model.addAttribute("content", "bills/unsold-bill-report"); // Reference to a Thymeleaf fragment

        return "admins";
    }

    @GetMapping("/reports/voided")
    public String getVoidedBillReport(@RequestParam(name = "month", required = false) Integer month,
                                      @RequestParam(name = "year", required = false) Integer year, Model model) {
        // Default to the current month if none is selected
        if (month == null) {
            //month = LocalDate.now().getMonthValue();
            month = 0;
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        // Fetch the list of sold bills for the selected month
        List<Bill> bills = billService.getVoidedBillsByMonthAndYear(month, year);

        // Calculate totals
        double totalConsumption = bills.stream().mapToDouble(Bill::getConsumption).sum();
        double totalAmount = bills.stream().mapToDouble(Bill::getAmount).sum();
        double totalMeterRent = bills.stream().mapToDouble(Bill::getMeterRent).sum();
        double totalServiceCharge = bills.stream().mapToDouble(Bill::getServiceCharge).sum();
        double totalPenalty = bills.stream().mapToDouble(Bill::getPenalty).sum();
        double totalBill = bills.stream().mapToDouble(Bill::getTotalBill).sum();
        // Add data to model
        model.addAttribute("bills", bills);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalMeterRent", totalMeterRent);
        model.addAttribute("totalServiceCharge", totalServiceCharge);
        model.addAttribute("totalPenalty", totalPenalty);
        model.addAttribute("totalBill", totalBill);
        model.addAttribute("totalConsumption", totalConsumption);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);//IntStream.rangeClosed(2020, LocalDate.now().getYear())
        //.boxed().collect(Collectors.toList()));
        model.addAttribute("content", "bills/voided-bill-report"); // Reference to a Thymeleaf fragment

        return "admins";
    }

    @GetMapping("/reports/export-pdf")
    public void exportBillReportToPdf(@RequestParam(name = "month", required = false) Integer month,
                                      @RequestParam(name = "year", required = false) Integer year,
                                      HttpServletResponse response) throws IOException, IOException, DocumentException {
        // Set response headers for PDF download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=bill-report.pdf");

        // Fetch the data again
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        } if (year == null) {
            year = LocalDate.now().getYear();
        }
        List<Bill> bills = billService.getSoldBillsByMonthAndYear(month, year);

        // Use a PDF generator (e.g., iText or Flying Saucer) to generate and write PDF to output stream
        pdfService.generateBillReportPdf(bills, month, year, "Sold bills of:  ",response.getOutputStream());
    }

    @GetMapping("/reports/unsold/export-pdf")
    public void exportUnsoldBillReportToPdf(@RequestParam(name = "month", required = false) Integer month,
                                            @RequestParam(name = "year", required = false) Integer year,
                                            HttpServletResponse response) throws IOException, IOException, DocumentException {
        // Set response headers for PDF download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=bill-report.pdf");

        // Fetch the data again
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        } if (year == null) {
            year = LocalDate.now().getYear();
        }
        List<Bill> bills = billService.getUnSoldBillsByMonthAndYear(month, year);

        // Use a PDF generator (e.g., iText or Flying Saucer) to generate and write PDF to output stream
        pdfService.generateBillReportPdf(bills, month, year, "Unsold bills of:  ",response.getOutputStream());
    }


    @GetMapping("/reports/voided/export-pdf")
    public void exportVoidedBillReportToPdf(@RequestParam(name = "month", required = false) Integer month,
                                            @RequestParam(name = "year", required = false) Integer year,
                                            HttpServletResponse response) throws IOException, IOException, DocumentException {
        // Set response headers for PDF download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=bill-report.pdf");

        // Fetch the data again
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        } if (year == null) {
            year = LocalDate.now().getYear();
        }
        List<Bill> bills = billService.getVoidedBillsByMonthAndYear(month, year);

        // Use a PDF generator (e.g., iText or Flying Saucer) to generate and write PDF to output stream
        pdfService.generateBillReportPdf(bills, month, year, "Voided bills of:  ", response.getOutputStream());
    }
}