package com.example.Nomia.controller;

import com.example.Nomia.model.Account;
import com.example.Nomia.model.BTransaction;
import com.example.Nomia.repository.AccountRepository;
import com.example.Nomia.repository.BTransactionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final BTransactionRepository transactionRepo;
    private final AccountRepository accountRepo;

    public TransactionController(BTransactionRepository transactionRepo, AccountRepository accountRepo) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
    }

    @GetMapping
    public String listTransactions(Model model) {
        model.addAttribute("transactions", transactionRepo.findAll());
        model.addAttribute("accounts", accountRepo.findAll());
        return "transactions";
    }

    @PostMapping("/assign")
    public String assignAccount(@RequestParam Long transactionId, @RequestParam Long accountId) {
        BTransaction transaction = transactionRepo.findById(transactionId).orElseThrow();
        Account account = accountRepo.findById(accountId).orElseThrow();
        transaction.setAccount(account);
        transactionRepo.save(transaction);
        return "redirect:/transactions";
    }
}
