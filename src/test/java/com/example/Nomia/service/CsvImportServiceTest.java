package com.example.Nomia.service;

import com.example.Nomia.model.BTransaction;
import com.example.Nomia.repository.BTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CsvImportServiceTest {

    @Mock
    private BTransactionRepository bTransactionRepository;

    @Mock
    private FileChecksumService fileChecksumService;

    @InjectMocks
    private CsvImportService csvImportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testImportCsv_HappyPath() throws IOException, NoSuchAlgorithmException {
        // Given = Arrange
        String csvContent = "Datum;Typ;Beskrivning;Belopp\n" +
                "31 mars 2025;Köp;=\" Apple.com/bill           , 020100529    \";-12\n";

        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));
        when(file.getBytes()).thenReturn(csvContent.getBytes(StandardCharsets.UTF_8));
        when(file.getOriginalFilename()).thenReturn("test.csv");
        when(fileChecksumService.isDuplicateFile(any())).thenReturn(false);

        BTransaction transaction = new BTransaction();
        transaction.setDate(LocalDate.of(2025, 3, 31));
        transaction.setAmount(BigDecimal.valueOf(100.00));

        when(bTransactionRepository.save(any(BTransaction.class))).thenReturn(transaction);

        // When = Act
        csvImportService.importCsv(file);

        // Then = Assert
        verify(bTransactionRepository, times(1)).save(any(BTransaction.class));
        verify(fileChecksumService, times(1)).saveChecksum(any(), anyString());
    }

    @Test
    void testImportCsv_EmptyLinesAndInvalidData() throws IOException, NoSuchAlgorithmException {
        String csvContent = "Datum;Typ;Beskrivning;Belopp\n" +
                "31 mars 2025;Köp;=\"Apple.com/bill, 020100529\";-12\n" +
                "1 april 2025;Mat;=\"ICA Kvantum\";-200\n" +
                "1 mars 2025;Elräkning;=\"Ellevio\";1200.50\n" +
                "   ;   ;    ;   \n" +  // Tom rad
                "30 februari 2025;Fel datum;=\"Test\";-500\n" +  // Ogiltigt datum
                "15 april 2025;För lång text;=\"" + "A".repeat(300) + "\";-600\n" +  // För lång text
                "15 april 2025;Fel format;=\"Test\";abc\n" +  // Felaktigt belopp
                ";;;\n" +  // Helt tom rad
                "1 april 2025;Mat;;-200\n";  // Tom beskrivning

        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn(csvContent.getBytes(StandardCharsets.UTF_8));
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));
        when(file.getOriginalFilename()).thenReturn("test.csv");
        when(fileChecksumService.isDuplicateFile(any())).thenReturn(false);

        csvImportService.importCsv(file);

        verify(bTransactionRepository, times(3)).save(any());
        verify(fileChecksumService).saveChecksum(any(), eq("test.csv"));
    }
}
