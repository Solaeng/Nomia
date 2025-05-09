package com.example.Nomia.repository;

import com.example.Nomia.model.FileChecksum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileChecksumRepository extends JpaRepository<FileChecksum, Long> {
    boolean existsByChecksum(String checksum);
}
