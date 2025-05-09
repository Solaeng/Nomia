package com.example.Nomia.service;

import com.example.Nomia.model.AccountGroup;
import com.example.Nomia.model.BTransaction;
import com.example.Nomia.repository.AccountGroupRepository;
import com.example.Nomia.repository.BTransactionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.IllegalArgumentException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
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
            if (fileChecksumService.isDuplicateFile(fileBytes)) {
              throw new IllegalArgumentException("Den här filen har redan lästs in.");
          }
            String line;
            boolean isFirstLine = true;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while ((line = reader.readLine()) != null) {

                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // hoppa över headern
                }

                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                /********************* DATE *********************************************/
                String dateStr = parts[0].trim();

                /********************* DESCRIPTION ***************************************/
                // =" Qstar Atran 0062         , Atran        "
                String text = parts[2].trim();
                try{
                    if (text.length() < 4) {
                        throw new IllegalArgumentException("Textfältet saknas. Kontrollera följande datum: " + dateStr);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Hoppar över rad pga fel: " + e.getMessage());
                    continue;
                }
                // Ta bort de tre första tecknen
                text = text.substring(3);
                // ta bort sista tecknet (vilket funkar om det alltid är ")
                text = StringUtils.chop(text).trim();
                try {
                    if (text == null || text.trim().isEmpty()) {
                        throw new IllegalArgumentException("Textfältet saknas. Kontrollera följande datum: " + dateStr);
                    }

                    if (text.length() > 255) {
                        throw new IllegalArgumentException("Textfältet överskrider 255 tecken." + text);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Hoppar över rad pga fel: " + e.getMessage());
                    continue;
                }

                /********************* AMOUNT ***************************************/
                String amountStr = parts[3].trim().replace(",", "."); // om svenska kommatecken



                BTransaction transaction = new BTransaction();
                try {
                    LocalDate dateToStr = parseSwedishDate(dateStr);
                    transaction.setDate(dateToStr);
                } catch (DateTimeParseException e) {
                    System.out.println("Ogiltigt datum: " + dateStr + " – hoppar över raden.");
                    continue;
                }
                transaction.setDescription(text);
                try {
                    BigDecimal amount = new BigDecimal(amountStr).abs();
                    transaction.setAmount(amount);
                } catch (NumberFormatException e) {
                    // Logga fel eller hantera det på något sätt
                    System.err.println("Ogiltigt belopp: " + amountStr);
                    continue;
                }

                bTransactionRepository.save(transaction);

            }
            fileChecksumService.saveChecksum(fileBytes);

        }

    }
}


