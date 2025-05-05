package com.example.Nomia.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accountgroup")
public class AccountGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @NotBlank
    @Column(name = "groupName", nullable = false)
    private String groupName;

   @OneToMany(mappedBy = "accountGroup", cascade = CascadeType.ALL)
    private List<Account> accountList = new ArrayList<>();

    // Getters and setters
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}

