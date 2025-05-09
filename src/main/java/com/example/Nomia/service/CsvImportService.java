package com.example.Nomia.service;

import com.example.Nomia.model.BTransaction;
import com.example.Nomia.repository.BTransactionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.IllegalArgumentException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Service
public class CsvImportService {

    @Autowired
    private BTransactionRepository bTransactionRepository;

    @Autowired
    private FileChecksumService fileChecksumService;

    // För att göra om 31 mars 2025 till 2025-03-31
    private final DateTimeFormatter swedishDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("sv", "SE"));

    private LocalDate parseSwedishDate(String dateStr) {
        return LocalDate.parse(dateStr.trim(), swedishDateFormatter);
    }

    public void importCsv(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            byte[] fileBytes = file.getBytes();
            String filename = file.getOriginalFilename();

            if (fileChecksumService.isDuplicateFile(fileBytes)) {
                throw new IllegalArgumentException("Den här filen har redan lästs in: " + filename);
            }
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {

                // hoppa över header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Dela upp raden
                String[] parts = line.split(";");
                if (parts.length < 4) {
                    System.out.println("Felaktigt format på rad: " + line);
                    continue;
                }

                String dateStr = parts[0].trim();
                String text = parts[2].trim();
                String amountStr = parts[3].trim().replace(",", "."); // om svenska kommatecken

                try {
                    //   Datumhantering
                    LocalDate date = parseSwedishDate(dateStr);

                    // Textfält (tar bort första tre och sista tecknet)   =" Qstar Atran 0062         , Atran        "
                    if (text.startsWith("=\"")) {
                        text = text.substring(2);
                    }
                    text = StringUtils.chop(text).trim();

                    if (text.length() < 4 || text.length() > 255 || text == null) {
                        throw new IllegalArgumentException("Textfältet är för kort, för långt eller blankt: " + text + " datum: " + dateStr);
                    }

                    // Belopp
                    BigDecimal amount = new BigDecimal(amountStr).abs();

                    // Skapa transaktion
                    BTransaction transaction = new BTransaction();
                    transaction.setDate(date);
                    transaction.setDescription(text);
                    transaction.setAmount(amount);

                    // Spara transaktion
                    bTransactionRepository.save(transaction);

                    // Logga fel eller hantera det på något sätt
                } catch (DateTimeParseException e) {
                    System.out.println("Ogiltigt datum: " + dateStr + " – hoppar över raden.");
                } catch (NumberFormatException e) {
                    System.out.println("Ogiltigt belopp: " + amountStr + " – hoppar över raden.");
                }                catch (IllegalArgumentException e) {
                    System.out.println("Felaktigt textfält eller annan validering misslyckades: " + e.getMessage());
                }
            }

            // Spara checksumman efter en lyckad import
                fileChecksumService.saveChecksum(fileBytes, filename);

            }
        }
    }