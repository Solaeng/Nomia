package com.example.Nomia.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "accountName")
    private String name;

    @ManyToOne
    @JoinColumn(name = "groupId")
    private AccountGroup accountGroup;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<BTransaction>  bTransactionList = new ArrayList<>();

    // Getters and setters
}
