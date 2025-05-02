package com.example.Nomia.model;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accountgroup")
public class AccountGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(name = "groupName")
    private String groupName;

   @OneToMany(mappedBy = "accountGroup", cascade = CascadeType.ALL)
    private List<Account> accountList = new ArrayList<>();

    // Getters and setters

    //    Vilken sida äger relationen?
    //    Hur vill du hämta kategorierna? (LAZY/EAGER)

  //  @OneToMany(fetch = FetchType.EAGER)
  //  private Account account;

  //  @OneToMany(mappedBy = "accountgruop", fetch = FetchType.EAGER)
}

