package com.example.Nomia.controller;

import com.example.Nomia.model.AccountGroup;
import com.example.Nomia.repository.AccountGroupRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-groups")
public class AccountGroupController {

    private final AccountGroupRepository accountGroupRepository;

    public AccountGroupController(AccountGroupRepository accountGroupRepository) {
        this.accountGroupRepository = accountGroupRepository;
    }

    @GetMapping
    public List<AccountGroup> getAll() {
        return accountGroupRepository.findAll();
    }

    @PostMapping
    public AccountGroup createGroup(@RequestBody AccountGroup group) {
        return accountGroupRepository.save(group);
    }

    @PutMapping("/{id}")
    public AccountGroup update(@PathVariable Long id, @RequestBody AccountGroup group) {
        AccountGroup existing = accountGroupRepository.findById(id).orElseThrow();
        existing.setGroupName(group.getGroupName());
        return accountGroupRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        accountGroupRepository.deleteById(id);
    }
}
