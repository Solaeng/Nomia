package com.example.Nomia.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "btransaction")
public class BTransaction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long transId;

        @Column(name = "date")
        private LocalDate date;
        @Column(name = "description")
        private String description;
        @Column(name = "amount")
        private BigDecimal amount;

        @ManyToOne
        @JoinColumn(name = "accountId")
        private Account account;

        public void setDate(LocalDate date) {
                this.date = date;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public void setAmount(BigDecimal amount) {
                this.amount = amount;
        }

        public void setAccount(Account account) {
                this.account = account;
        }

        // Getters and setters
    }
