package com.example.Nomia.repository;

import com.example.Nomia.model.BTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BTransactionRepository extends JpaRepository<BTransaction, Long> {

}
