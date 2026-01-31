package com.example.demo.controller;

import com.example.demo.dto.ProductSalesRow;
import com.example.demo.service.ReportingService;
import com.itextpdf.text.Element;
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

    @Autowired
    SalesReportsController(ReportingService svc) {
        this.svc = svc;
    }

    private void exportSalesPdf(HttpServletResponse response,
            String title, String subtitle,
            List<ProductSalesRow> rows) throws Exception {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                title.toLowerCase().replace(' ', '_') + ".pdf");
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 36, 36, 36, 36);
        com.itextpdf.text.pdf.PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();
        com.itextpdf.text.Font h1 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14,
                com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font h2 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11);
        com.itextpdf.text.Font th = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
                com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font td = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);
        com.itextpdf.text.Paragraph pTitle = new com.itextpdf.text.Paragraph(title, h1);
        pTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        doc.add(pTitle);
        com.itextpdf.text.Paragraph pSub = new com.itextpdf.text.Paragraph(subtitle, h2);
        pSub.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        doc.add(pSub);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // 5 columns: Date, Product, Qty, Profit/Loss, Amount
        com.itextpdf.text.pdf.PdfPTable t = new com.itextpdf.text.pdf.PdfPTable(5);
        t.setWidthPercentage(100);
        t.setWidths(new float[] { 2f, 3f, 1f, 2f, 2f });
        addHeaderCell(t, "Date", th);
        addHeaderCell(t, "Product", th);
        addHeaderCell(t, "Qty", th);
        addHeaderCell(t, "P/L", th);
        addHeaderCell(t, "Amount", th);

        long totalQty = 0L;
        double totalPL = 0.0;
        for (ProductSalesRow r : rows) {
            long q = (r.getQty() != null) ? r.getQty() : 0L;
            double pl = (r.getProfitLossAmount() != null) ? r.getProfitLossAmount() : 0.0;

            addCell(t, r.getDate() != null ? r.getDate().toString() : "-", td);
            addCell(t, r.getProductName(), td);
            addCell(t, String.valueOf(q), td);
            addCell(t, pl >= 0 ? "Profit" : "Loss", td);
            addCell(t, money(pl), td);

            totalQty += q;
            totalPL += pl;
        }

        com.itextpdf.text.pdf.PdfPCell totLabel = new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase("TOTAL", th));
        totLabel.setColspan(2);
        t.addCell(totLabel);
        addCell(t, String.valueOf(totalQty), th);
        addCell(t, "", th); // Empty cell for P/L column in total row
        addCell(t, money(totalPL), th);

        doc.add(t);
        doc.close();
    }

    @GetMapping("/daily.pdf")
    public void dailyPdf(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletResponse response) throws Exception {
        LocalDate d = (date != null) ? date : LocalDate.now();
        List<ProductSalesRow> rows = svc.daily(d);
        exportSalesPdf(response, "Daily Sales - " + d, "Date: " + d, rows);
    }

    @GetMapping("/monthly.pdf")
    public void monthlyPdf(@RequestParam int year, @RequestParam int month, HttpServletResponse response)
            throws Exception {
        YearMonth ym = YearMonth.of(year, month);
        List<ProductSalesRow> rows = svc.monthly(ym);
        exportSalesPdf(response, "Monthly Sales - " + ym, "Period: " + ym, rows);
    }

    private static void addHeaderCell(com.itextpdf.text.pdf.PdfPTable t, String text, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        t.addCell(c);
    }

    private static void addCell(com.itextpdf.text.pdf.PdfPTable t, String text, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(c);
    }

    private static String money(double v) {
        return String.format("%.2f", v);
    }

    @GetMapping("/daily")
    public String dailyHtml(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate d = (date != null) ? date : LocalDate.now();
        List<ProductSalesRow> rows = svc.daily(d);
        model.addAttribute("date", d);
        model.addAttribute("rows", rows);
        return "daily_sales";
    }

    @GetMapping("/monthly")
    public String monthlyHtml(@RequestParam int year, @RequestParam int month, Model model) {
        YearMonth ym = YearMonth.of(year, month);
        List<ProductSalesRow> rows = svc.monthly(ym);
        long totalQty = rows.stream().mapToLong(r -> r.getQty() == null ? 0L : r.getQty()).sum();
        double totalRevenue = rows.stream().mapToDouble(r -> r.getTotalRevenue() == null ? 0.0 : r.getTotalRevenue())
                .sum();
        double totalPL = rows.stream().mapToDouble(r -> r.getProfitLossAmount() == null ? 0.0 : r.getProfitLossAmount())
                .sum();

        model.addAttribute("ym", ym);
        model.addAttribute("rows", rows);
        model.addAttribute("totalQty", totalQty);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalPL", totalPL);
        return "monthly_sales";
    }
}
