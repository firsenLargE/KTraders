package com.example.demo.controller;

import com.example.demo.entity.CashTransaction;
import com.example.demo.service.CashLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.*;

@Controller @RequestMapping("/reports/cash")
public class CashLedgerController {
    private final CashLedgerService ledger;
    @Autowired CashLedgerController(CashLedgerService ledger) { this.ledger = ledger; }

    @GetMapping
    public String view(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                       @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
                       @RequestParam(required=false) String type,
                       @RequestParam(required=false) String method,
                       @RequestParam(required=false) Boolean settled,
                       Model model) {

        LocalDate start = (from!=null) ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end   = (to  !=null) ? to   : LocalDate.now();

        Set<CashTransaction.Type> types = new HashSet<>();
        if (type==null || type.isBlank() || "ALL".equalsIgnoreCase(type)) {
            types = Set.of(CashTransaction.Type.IN, CashTransaction.Type.OUT);
        } else {
            types.add(CashTransaction.Type.valueOf(type.toUpperCase(Locale.ROOT)));
        }

        Set<CashTransaction.PaymentMethod> methods = new HashSet<>();
        if (method!=null && !method.isBlank()) {
            for (String m : method.split(",")) {
                methods.add(CashTransaction.PaymentMethod.valueOf(m.trim().toUpperCase(Locale.ROOT)));
            }
        }

        CashLedgerService.Result r = ledger.view(start, end, types, methods, settled);
        model.addAttribute("rows", r.rows);
        model.addAttribute("totalIn",  r.totalIn);
        model.addAttribute("totalOut", r.totalOut);
        model.addAttribute("net",      r.net);
        model.addAttribute("totalsByMethod", r.totalsByMethod);
        model.addAttribute("remaining", r.remaining);

        model.addAttribute("from", start);
        model.addAttribute("to", end);
        model.addAttribute("type", type==null ? "ALL" : type.toUpperCase(Locale.ROOT));
        model.addAttribute("methodCsv", method==null ? "" : method);
        model.addAttribute("settled", settled);
        return "cash_ledger";
    }
}


