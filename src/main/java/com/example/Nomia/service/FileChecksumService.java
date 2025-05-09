package com.example.Nomia.service;

import com.example.Nomia.model.FileChecksum;
import com.example.Nomia.repository.FileChecksumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;


@Service
public class FileChecksumService {

    @Autowired
    private FileChecksumRepository fileChecksumRepository;

    public boolean isDuplicateFile(byte[] fileBytes) throws NoSuchAlgorithmException {
        String checksum = calculateChecksum(fileBytes);
        return fileChecksumRepository.existsByChecksum(checksum);
    }

    public void saveChecksum(byte[] fileBytes, String filename) throws NoSuchAlgorithmException {
        String checksum = calculateChecksum(fileBytes);
        LocalDateTime uploaded_date = LocalDateTime.now();
       // String filename = "Annas fil";
                //multipartFile.getOriginalFilename();

        FileChecksum fileChecksum = new FileChecksum(checksum, filename, uploaded_date);
        fileChecksumRepository.save(fileChecksum);

    }

    private String calculateChecksum(byte[] fileBytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(fileBytes);
        return HexFormat.of().formatHex(hash);
    }
}
