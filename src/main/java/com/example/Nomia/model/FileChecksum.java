package com.example.Nomia.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "filechecksum")
public class FileChecksum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(nullable = false)
    private String filename;

    @Column(name = "checksum", nullable = false, unique = true)
    private String checksum;

    @Column(nullable = false)
    private LocalDateTime uploadedDate;

    public FileChecksum() {
    }

    public FileChecksum(String checksum, String filename, LocalDateTime uploadedDate) {
        this.checksum = checksum;
        this.filename = filename;
        this.uploadedDate = uploadedDate;
    }

    public Long getFileId() {
        return fileId;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(LocalDateTime uploadedDate) {
        this.uploadedDate = uploadedDate;
    }
}