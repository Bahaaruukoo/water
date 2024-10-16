package com.UserManagement.billsystem.services;

import com.UserManagement.billsystem.entities.Bill;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;


import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class PdfService {

    public void generateBillReportPdf(List<Bill> bills, int month, int year, String message, OutputStream outputStream) throws DocumentException {

        // Set the document to landscape orientation
        Document document = new Document(PageSize.A4.rotate());
        //Document document = new Document();
        document.setMargins(5, 5, 20, 20); // left, right, top, bottom
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Set a custom font size
        Font font = new Font(Font.FontFamily.HELVETICA, 10); // Font with size 10

        // Add title
        LocalDate localDate = LocalDate.of(year, month, 1);
        document.add(new Paragraph(message +" "+localDate.getMonth() +" / " + year));

        // Add table
        PdfPTable table = new PdfPTable(11);

        // Set custom column widths (these values are proportional)
        float[] columnWidths = {3f, 7f, 1f, 3f, 1f, 3f, 3f, 2f, 3f, 3f, 2f};
        table.setWidths(columnWidths); // Apply the custom column widths

        // Set table width to 100% of available document width
        table.setWidthPercentage(100);
        // Add table headers
        table.addCell(new PdfPCell(new Phrase("Bill No", font)));
        table.addCell(new PdfPCell(new Phrase("Customer Name", font)));//.setColspan(15);
        table.addCell(new PdfPCell(new Phrase("Kebele", font)));
        table.addCell(new PdfPCell(new Phrase("Mtr No", font)));
        table.addCell(new PdfPCell(new Phrase("Mtr Size", font)));
        table.addCell(new PdfPCell(new Phrase("Prev Reading", font)));
        table.addCell(new PdfPCell(new Phrase("Curr Reading", font)));
        table.addCell(new PdfPCell(new Phrase("Cons", font)));
        table.addCell(new PdfPCell(new Phrase("Cons Cost", font)));
        table.addCell(new PdfPCell(new Phrase("Total Cost", font)));
        table.addCell(new PdfPCell(new Phrase("Status", font)));

        for (Bill bill : bills) {
            table.addCell(new PdfPCell(new Phrase(bill.getBillNumber(), font)));
            table.addCell(new PdfPCell(new Phrase(bill.getCustomer().getName(), font)));
            table.addCell(new PdfPCell(new Phrase(bill.getCustomer().getKebele(), font)));
            table.addCell(new PdfPCell(new Phrase(bill.getCustomer().getMeter().getMeterNumber(), font)));
            table.addCell(new PdfPCell(new Phrase(bill.getCustomer().getMeter().getMeterSize(), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(bill.getPreviousReading()), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(bill.getCurrentReading()), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(bill.getConsumption()), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(bill.getAmount()), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(bill.getTotalBill()), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(bill.getStatus()), font)));
        }

        // Calculate totals
        double totalAmount = bills.stream().mapToDouble(Bill::getAmount).sum();
        double totalMeterRent = bills.stream().mapToDouble(Bill::getMeterRent).sum();
        double totalServiceCharge = bills.stream().mapToDouble(Bill::getServiceCharge).sum();
        double totalPenalty = bills.stream().mapToDouble(Bill::getPenalty).sum();
        double totalBill = bills.stream().mapToDouble(Bill::getTotalBill).sum();
        double totalConsumption = bills.stream().mapToDouble(Bill::getConsumption).sum();


        table.addCell(new PdfPCell(new Phrase()));
        table.addCell(new PdfPCell(new Phrase()));
        table.addCell(new PdfPCell(new Phrase()));
        table.addCell(new PdfPCell(new Phrase()));
        table.addCell(new PdfPCell(new Phrase()));
        table.addCell(new PdfPCell(new Phrase()));
        table.addCell("Total");
        table.addCell(new PdfPCell(new Phrase(String.valueOf(totalConsumption), font)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(totalAmount), font)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(totalBill), font)));
        table.addCell(new PdfPCell(new Phrase()));

        document.add(table);
        document.close();
    }
}
