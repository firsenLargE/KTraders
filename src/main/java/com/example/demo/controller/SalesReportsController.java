package com.example.demo.controller;

import com.example.demo.dto.ProductSalesRow;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ReportingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/reports/sales")
public class SalesReportsController {
    private final ReportingService svc;
    private final CustomerService customerService;

    @Autowired
    SalesReportsController(ReportingService svc, CustomerService customerService) {
        this.svc = svc;
        this.customerService = customerService;
    }

    private void exportSalesPdf(HttpServletResponse response,
            String title, String subtitle,
            List<ProductSalesRow> rows, boolean isBill) throws Exception {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                title.toLowerCase().replace(' ', '_') + ".pdf");
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 36, 36, 36, 36);
        com.itextpdf.text.pdf.PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();
        com.itextpdf.text.Font h1 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16,
                com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font h2 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);
        com.itextpdf.text.Font th = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
                com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font td = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

        com.itextpdf.text.Paragraph pTitle = new com.itextpdf.text.Paragraph(isBill ? "INVOICE / BILL" : title, h1);
        pTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        doc.add(pTitle);

        com.itextpdf.text.Paragraph pSub = new com.itextpdf.text.Paragraph(subtitle, h2);
        pSub.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        doc.add(pSub);

        if (isBill && !rows.isEmpty()) {
            doc.add(new com.itextpdf.text.Paragraph("Customer: " + rows.get(0).getCustomerName(), h2));
            doc.add(new com.itextpdf.text.Paragraph(" "));
        }
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // 6 columns: Date, Product, Customer (if not bill), Qty, Status, Net Earnings
        int cols = isBill ? 5 : 6;
        com.itextpdf.text.pdf.PdfPTable t = new com.itextpdf.text.pdf.PdfPTable(cols);
        t.setWidthPercentage(100);
        if (isBill) {
            t.setWidths(new float[] { 2f, 4f, 1f, 2f, 2f });
        } else {
            t.setWidths(new float[] { 2f, 3f, 3f, 1f, 2f, 2f });
        }

        addHeaderCell(t, "Date", th);
        addHeaderCell(t, "Item Name", th);
        if (!isBill)
            addHeaderCell(t, "Customer", th);
        addHeaderCell(t, "Qty", th);
        addHeaderCell(t, "Status", th);
        addHeaderCell(t, "Net Earnings", th);

        long totalQty = 0L;
        double totalPL = 0.0;
        for (ProductSalesRow r : rows) {
            long q = (r.getQty() != null) ? r.getQty() : 0L;
            double pl = (r.getProfitLossAmount() != null) ? r.getProfitLossAmount() : 0.0;

            addCell(t, r.getDate() != null ? r.getDate().toString() : "-", td);
            addCell(t, r.getProductName(), td);
            if (!isBill)
                addCell(t, r.getCustomerName() != null ? r.getCustomerName() : "Walk-in", td);
            addCell(t, String.valueOf(q), td);
            addCell(t, pl >= 0 ? "Gain" : "Shortfall", td);
            addCell(t, money(pl), td);

            totalQty += q;
            totalPL += pl;
        }

        com.itextpdf.text.pdf.PdfPCell totLabel = new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase("TOTAL CUMULATIVE", th));
        totLabel.setColspan(isBill ? 2 : 3);
        t.addCell(totLabel);
        addCell(t, String.valueOf(totalQty), th);
        addCell(t, "", th);
        addCell(t, money(totalPL), th);

        doc.add(t);
        doc.close();
    }

    @GetMapping("/daily")
    public String dailyHtml(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate d = (date != null) ? date : LocalDate.now();
        List<ProductSalesRow> rows = svc.daily(d);
        long totalQty = rows.stream().mapToLong(r -> r.getQty() == null ? 0L : r.getQty()).sum();
        double totalPL = rows.stream().mapToDouble(r -> r.getProfitLossAmount() == null ? 0.0 : r.getProfitLossAmount())
                .sum();

        model.addAttribute("date", d);
        model.addAttribute("rows", rows);
        model.addAttribute("totalQty", totalQty);
        model.addAttribute("totalPL", totalPL);
        return "daily_sales";
    }

    @GetMapping("/daily.pdf")
    public void dailyPdf(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletResponse response) throws Exception {
        LocalDate d = (date != null) ? date : LocalDate.now();
        List<ProductSalesRow> rows = svc.daily(d);
        exportSalesPdf(response, "Daily Sales - " + d, "Date: " + d, rows, false);
    }

    @GetMapping("/monthly.pdf")
    public void monthlyPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId,
            HttpServletResponse response) throws Exception {

        List<ProductSalesRow> rows;
        String subtitle;
        boolean isBill = customerId != null;

        if (startDate != null && endDate != null) {
            rows = svc.filter(startDate, endDate, customerId);
            subtitle = "Period: " + startDate + " to " + endDate;
        } else if (year != null && month != null) {
            YearMonth ym = YearMonth.of(year, month);
            rows = svc.filter(ym.atDay(1), ym.atEndOfMonth(), customerId);
            subtitle = "Period: " + ym;
        } else {
            YearMonth ym = YearMonth.now();
            rows = svc.filter(ym.atDay(1), ym.atEndOfMonth(), customerId);
            subtitle = "Period: " + ym;
        }

        exportSalesPdf(response, "Sales Report", subtitle, rows, isBill);
    }

    @GetMapping("/monthly")
    public String monthlyHtml(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId,
            Model model) {

        List<ProductSalesRow> rows;
        YearMonth ym = null;

        if (startDate != null && endDate != null) {
            rows = svc.filter(startDate, endDate, customerId);
        } else if (year != null && month != null) {
            ym = YearMonth.of(year, month);
            rows = svc.filter(ym.atDay(1), ym.atEndOfMonth(), customerId);
        } else {
            ym = YearMonth.now();
            rows = svc.filter(ym.atDay(1), ym.atEndOfMonth(), customerId);
        }

        long totalQty = rows.stream().mapToLong(r -> r.getQty() == null ? 0L : r.getQty()).sum();
        double totalRevenue = rows.stream().mapToDouble(r -> r.getTotalRevenue() == null ? 0.0 : r.getTotalRevenue())
                .sum();
        double totalPL = rows.stream().mapToDouble(r -> r.getProfitLossAmount() == null ? 0.0 : r.getProfitLossAmount())
                .sum();

        model.addAttribute("ym", ym);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("customerId", customerId);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("rows", rows);
        model.addAttribute("totalQty", totalQty);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalPL", totalPL);
        return "monthly_sales";
    }

    private static void addHeaderCell(com.itextpdf.text.pdf.PdfPTable t, String text, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
        c.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        t.addCell(c);
    }

    private static void addCell(com.itextpdf.text.pdf.PdfPTable t, String text, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
        c.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
        t.addCell(c);
    }

    private static String money(double v) {
        return String.format("%.2f", v);
    }
}
