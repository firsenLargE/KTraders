package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Product;
import com.example.demo.entity.Sale;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ProductService;
import com.example.demo.service.SaleService;
import com.example.demo.util.NumberToWordsConverter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.time.LocalDate;

@Controller
public class SaleController {
    @Autowired private SaleService saleService;
    @Autowired private ProductService productService;
    @Autowired private CustomerService customerService;

    @GetMapping("/sales")
    public String showSalesForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("sale", new Sale());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("sales", saleService.getAllSales());
        model.addAttribute("customer", new Customer());
        return "sales";
    }

    @GetMapping("/sales/add")
    public String showAddSaleForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("sale", new Sale());
        model.addAttribute("products", productService.getAllProducts());
        return "add-sale";
    }

    @PostMapping("/sales/add")
    public String addSale(@ModelAttribute Sale sale, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/sales";
        try {
            Product product = productService.getProductById(sale.getProduct().getId());
            Customer customer = customerService.getCustomerById(sale.getCustomer().getId());
            if (product == null || sale.getQuantity() == null || sale.getQuantity() <= 0) {
                ra.addFlashAttribute("error", "Invalid product or quantity"); return "redirect:/sales";
            }
            if (product.getQuantity() == null || sale.getQuantity() > product.getQuantity()) {
                ra.addFlashAttribute("error", "Insufficient stock"); return "redirect:/sales";
            }

            Double unit = (sale.getUnitSellingPrice() != null && sale.getUnitSellingPrice() > 0)
                    ? sale.getUnitSellingPrice()
                    : (product.getPrice() != null ? product.getPrice() : 0.0);

            sale.setProduct(product);
            sale.setCustomer(customer);
            sale.setUnitSellingPrice(unit);
            sale.setTotalPrice(unit * sale.getQuantity());
            sale.setDate(LocalDate.now());

            product.setQuantity(product.getQuantity() - sale.getQuantity());
            productService.updateProduct(product);

            saleService.addSale(sale);
            ra.addFlashAttribute("success", "Sale added!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/sales";
    }

    @GetMapping("/sales/{id}/receipt")
    public void generateReceipt(@PathVariable Integer id, HttpServletResponse response) throws IOException, DocumentException {
        Sale sale = saleService.getSaleById(id);
        if (sale == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Sale not found"); return; }
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=invoice_"+id+".pdf");
        Document doc = new Document(PageSize.A4,36,36,36,36);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();
        Font headerFont = new Font(Font.FontFamily.HELVETICA,14,Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA,10);
        Font boldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD);
        Paragraph store = new Paragraph("KAPIL TRADERS", headerFont); store.setAlignment(Element.ALIGN_CENTER); doc.add(store);
        Paragraph subHeader = new Paragraph("Address: Kathmandu, Nepal | PAN/VAT: 123456789", normalFont); subHeader.setAlignment(Element.ALIGN_CENTER); doc.add(subHeader);
        doc.add(new Paragraph(" "));
        PdfPTable buyerTable = new PdfPTable(3); buyerTable.setWidthPercentage(100);
        buyerTable.addCell(new Phrase("Buyer Name: " + (sale.getCustomer()!=null?sale.getCustomer().getName():"Walk-in"), normalFont));
        buyerTable.addCell(new Phrase("Invoice No: " + sale.getId(), normalFont));
        buyerTable.addCell(new Phrase("Buyer PAN: " + (sale.getCustomer()!=null && sale.getCustomer().getPanNumber()!=null?sale.getCustomer().getPanNumber():"-"), normalFont));
        buyerTable.addCell(new Phrase("Date: " + sale.getDate(), normalFont));
        buyerTable.addCell(new Phrase("VAT No: " + (sale.getCustomer()!=null && sale.getCustomer().getVatNumber()!=null?sale.getCustomer().getVatNumber():"-"), normalFont));
        buyerTable.addCell(new Phrase("Contact: " + (sale.getCustomer()!=null?sale.getCustomer().getContact():"-"), normalFont));
        doc.add(buyerTable);
        doc.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(6); table.setWidthPercentage(100); table.setWidths(new float[]{3,1,1,2,2,2});
        table.addCell(new Phrase("Item", boldFont));
        table.addCell(new Phrase("Qty", boldFont));
        table.addCell(new Phrase("Rate", boldFont));
        table.addCell(new Phrase("Taxable", boldFont));
        table.addCell(new Phrase("VAT (13%)", boldFont));
        table.addCell(new Phrase("Total", boldFont));
        double unitPrice = (sale.getUnitSellingPrice()!=null) ? sale.getUnitSellingPrice()
                  : (sale.getProduct()!=null && sale.getProduct().getPrice()!=null ? sale.getProduct().getPrice() : 0.0);
        int qty = (sale.getQuantity()!=null) ? sale.getQuantity() : 0;
        double taxable = unitPrice * qty;
        double vat = taxable * 0.13;
        double gross = taxable + vat;
        table.addCell(sale.getProduct()!=null ? sale.getProduct().getName() : "-");
        table.addCell(String.valueOf(qty));
        table.addCell(String.format("%.2f", unitPrice));
        table.addCell(String.format("%.2f", taxable));
        table.addCell(String.format("%.2f", vat));
        table.addCell(String.format("%.2f", gross));
        PdfPCell empty = new PdfPCell(new Phrase("")); empty.setColspan(3); table.addCell(empty);
        table.addCell(String.format("%.2f", taxable));
        table.addCell(String.format("%.2f", vat));
        table.addCell(String.format("%.2f", gross));
        doc.add(table);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Amount in words: " + NumberToWordsConverter.convert((long) gross) + " only.", normalFont));
        doc.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Authorized Signature", boldFont); footer.setAlignment(Element.ALIGN_RIGHT); doc.add(footer);
        doc.close();
    }
}


