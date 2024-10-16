package com.UserManagement.billsystem.services;

import com.UserManagement.GlobalSetting;
import com.UserManagement.billsystem.dto.KebeleCategoryBillReportDTO;
import com.UserManagement.billsystem.entities.Bill;
import com.UserManagement.billsystem.entities.Customer;
import com.UserManagement.billsystem.entities.MeterReading;
import com.UserManagement.billsystem.entities.Settings;
import com.UserManagement.billsystem.repositories.BillRepository;
import com.UserManagement.billsystem.repositories.CustomerRepository;
import com.UserManagement.billsystem.repositories.MeterReadingRepository;
import com.UserManagement.billsystem.repositories.SettingsRepository;
import com.UserManagement.enums.BillGenerationStatus;
import com.UserManagement.enums.BillPaymentStatus;
import com.UserManagement.enums.CustomerCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class BillService {
    private final CustomerRepository customerRepository;
    private final BillRepository billRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final SettingsRepository settingsRepository;

    public BillService(CustomerRepository customerRepository, BillRepository billRepository,
                       MeterReadingRepository meterReadingRepository, SettingsRepository settingsRepository ) {
        this.customerRepository = customerRepository;
        this.billRepository = billRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.settingsRepository = settingsRepository;
    }
    public List<Bill> findAllBills() {
        return billRepository.findAll();
    }

    public Page<Bill> getPaginatedBills(Pageable pageable) {
        return billRepository.findAll(pageable);
    }
    @Transactional
    public void voidBill(Long id) {
        Bill bill = billRepository.findById(id).orElseThrow();
        bill.setStatus(BillPaymentStatus.VOIDED);
        MeterReading meterReading = bill.getMeterReading();
        meterReading.setBillGenerationStatus(BillGenerationStatus.VOIDED);
        billRepository.save(bill);
        meterReadingRepository.save(meterReading);
    }

    // Cron expression: "0 0 0 5,6 * *" means at midnight on 5th, 6th day of each month
    @Scheduled(cron = "0 0 0 5,6 * *")
    //@Scheduled(cron = "0 */2 * * * ?")  //every two minutes
    public void generateBills() {
        // Fetch customers due for bill generation
        // find customers with active meter status.
        // find current and previous reading for a customer
        // calculate the consumption of the month & cost
        // create bill and save it to the bill table
        // update the status of the current reading

        //List<Customer> customers = customerRepository.findAll();
        //List<Customer> customers = customerRepository.findByServiceStatus("Active");
        List<MeterReading> readings = meterReadingRepository.findByBillGenerationStatus(BillGenerationStatus.CAPTURED);
        List<Bill> bills = new ArrayList<>();
        // Generate bills for all READINGS (you can filter based on custom logic)
        for (MeterReading reading : readings) {
            bills.add( generateBillForAReading(reading) );
            reading.setBillGenerationStatus(BillGenerationStatus.GENERATED);
        }

        billRepository.saveAll(bills);
        meterReadingRepository.saveAll(readings);
        System.out.println("bills generated");

    }

    private Bill generateBillForAReading(MeterReading reading) {
        Bill bill = new Bill();
        Settings settings = settingsRepository.findAll().get(0);

        //System.out.println(settings.toString());
        bill.setCustomer(reading.getMeter().getCustomer());
        bill.setBillNumber(reading.getMeter().getCustomer().getBillNumber());
        bill.setMeterReading(reading);
        bill.setInvoiceDate(LocalDate.now());
        bill.setDueDate(LocalDate.now().plusDays(GlobalSetting.globalSettings.getDueDateAfter()));
        bill.setStatus(BillPaymentStatus.UNSOLD);
        bill.setMeterRent(settings.getMeterRentalPrice());
        bill.setServiceCharge(settings.getServiceCharge());
        bill.setPenalty(settings.getLatePaymentFee());

        MeterReading prevMeterReading = meterReadingRepository.findPreviousReading(reading.getMeter().getId(), reading.getReadingDate());

        bill.setPreviousMeterReading(prevMeterReading); //reference

        bill.setCurrentReading(reading.getMeterValue()); //value
        bill.setPreviousReading(prevMeterReading.getMeterValue());
        double currentMeterReadValue = bill.getCurrentReading();

        double prevReadingValue = bill.getPreviousReading();
        double thisPeriodConsumption = currentMeterReadValue - prevReadingValue;
        bill.setConsumption(thisPeriodConsumption);
        double consumptionCost = 0;
        double blockCost [] = new double[6];
        CustomerCategory category = bill.getCustomer().getCustomerCategory();

        int blockIndex = 0;
        blockCost[0] = GlobalSetting.globalPrices.get(category).getBlockOne();
        blockCost[1] = GlobalSetting.globalPrices.get(category).getBlockTwo();
        blockCost[2] = GlobalSetting.globalPrices.get(category).getBlockThree();
        blockCost[3] = GlobalSetting.globalPrices.get(category).getBlockFour();
        blockCost[4] = GlobalSetting.globalPrices.get(category).getBlockFive();
        blockCost[5] = GlobalSetting.globalPrices.get(category).getBlockSix();

        while(thisPeriodConsumption > 5 && blockIndex < 5){
            consumptionCost +=  5 * blockCost[blockIndex] ;
            thisPeriodConsumption -= 5;
            blockIndex++;
        }
        consumptionCost += thisPeriodConsumption * blockCost[blockIndex];
        bill.setAmount(consumptionCost);
/*
        //Calculate penalty
        long delay = 0;
        if(LocalDate.now().isAfter(bill.getDueDate())){
            delay = ChronoUnit.DAYS.between(bill.getDueDate(), LocalDate.now());
        }
        bill.setPenalty(GlobalSetting.globalSettings.getLatePaymentFee() * delay);
*/
        bill.setTotalBill( bill.getAmount() + bill.getPenalty() + bill.getInterest() + bill.getMeterRent() + bill.getServiceCharge());


        return bill;
    }

    @Transactional
    public void billRequest(Long id){
        MeterReading meterReading = meterReadingRepository.findById(id).orElseThrow( () ->new RuntimeException("Reading not found"));

        if(Objects.equals(meterReading.getBillGenerationStatus(), BillGenerationStatus.GENERATED)){
            return ; //"Bill already generated"
        }
        Bill bill = generateBillForAReading(meterReading);
        meterReading.setBillGenerationStatus(BillGenerationStatus.GENERATED);

        billRepository.save(bill);
        meterReadingRepository.save(meterReading);
    }

    public Bill viewBillDetail(Long id) {
        return billRepository.findById(id).orElseThrow(null);
    }

    public double getTotalUnpaidBills() {
        return billRepository.sumUnpaidBills();
    }

    public double getTotalCollectedAmount() {
        return billRepository.sumCollectedAmount();
    }

    public double getUpcomingDueBills() {
        LocalDate endDate = LocalDate.now().plusDays(7); // Set range for the upcoming week
        return billRepository.findUpcomingDueBills(endDate);
    }

    public double getOverdueBills() {
        return billRepository.findOverdueBills();
    }

    public Bill findById(Long id) {
        return billRepository.findById(id).get();
    }

    public void save(Bill bill) {
        billRepository.save(bill);
    }

    public List<Bill> findSoldBillsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return billRepository.findByStatusAndSoldDateBetween(BillPaymentStatus.SOLD, startDate, endDate);
    }

    public List<Bill> findSoldBillsByDateRange(LocalDate startDate, LocalDate endDate) {
        return billRepository.findBySoldDateBetween(startDate, endDate);

    }
    public List<KebeleCategoryBillReportDTO> getGroupedBillReport() {
        return billRepository.getGroupedBillReport();
    }
    public List<Bill> getSoldBillsByMonthAndYear(int month, int year) {
        if(month == 0){
            return billRepository.findByPaymentStatusAndYear(BillPaymentStatus.SOLD, year);
        }
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return billRepository.findByStatusAndSoldDateBetween(BillPaymentStatus.SOLD, startDate, endDate);
    }

    public List<Bill> getUnSoldBillsByMonthAndYear(int month, int year) {
        if(month == 0){
            return billRepository.findByPaymentStatusAndYear(BillPaymentStatus.UNSOLD, year);
        }
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        //List<Bill> bills = billRepository.findByStatusAndSoldDateBetween(BillPaymentStatus.UNSOLD, startDate, endDate);
        //since it is not unsold, it cannot based on sold date
        List<Bill> bills = billRepository.findByStatusAndInvoiceDateBetween(BillPaymentStatus.UNSOLD, startDate, endDate);

        return bills;
    }
    public List<Bill> getVoidedBillsByMonthAndYear(int month, int year) {
        if(month == 0){
            return billRepository.findByPaymentStatusAndYear(BillPaymentStatus.VOIDED, year);
        }
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        //since it is not unsold, it cannot base on sold date
        List<Bill> bills = billRepository.findByStatusAndInvoiceDateBetween(BillPaymentStatus.VOIDED, startDate, endDate);

        return bills;
    }

    public Page<Bill> getFilteredBills(String billNumber, String status, Pageable pageable) {
        if ((billNumber != null && !billNumber.isEmpty()) && (status != null && !status.isEmpty())) {
            // Both billNumber and status are provided
            return billRepository.findByBillNumberContainingAndStatus(billNumber, BillPaymentStatus.valueOf(status), pageable);
        } else if (billNumber != null && !billNumber.isEmpty()) {
            // Only billNumber is provided
            return billRepository.findByBillNumberContaining(billNumber, pageable);
        } else if (status != null && !status.isEmpty()) {
            // Only status is provided
            return billRepository.findByStatus(BillPaymentStatus.valueOf(status), pageable);
        } else {
            // No filters applied
            return billRepository.findAll(pageable);
        }
    }
}