package at.atypicalbacklog.management.importing.parser;

import at.atypicalbacklog.management.importing.ImportType;
import at.atypicalbacklog.management.importing.domain.ImportRecord;
import at.atypicalbacklog.management.importing.parser.model.ParsedGameInstanceRow;
import at.atypicalbacklog.management.importing.parser.model.ParsedTimeBookingRow;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.nio.charset.StandardCharsets;

@Service
public class ImportCsvParserService {

    private final ObjectMapper objectMapper;
    public ImportCsvParserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
        DateTimeFormatter.ofPattern("dd.M.yyyy"),
        DateTimeFormatter.ofPattern("d.MM.yyyy"),
        DateTimeFormatter.ofPattern("d.M.yyyy")
    };

    /**
     * Parses a LocalDate from a string using multiple date formats:
     * dd.MM.yyyy, dd.M.yyyy, d.MM.yyyy, d.M.yyyy
     * Returns null if the input is null, empty, or blank.
     */
    private LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        String trimmed = dateString.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }
        throw new IllegalArgumentException("Unable to parse date: " + dateString + ". Expected format: dd.MM.yyyy, dd.M.yyyy, d.MM.yyyy, or d.M.yyyy");
    }

    /**
     * Parses a Duration from a string. Supports:
     * - "H:mm" or "HH:mm" (e.g. "1:30" = 1h 30m, "00:00" = zero)
     * - ISO-8601 duration (e.g. "PT1H30M") as fallback
     */
    private Duration parseDuration(String durationString) {
        if (durationString == null || durationString.isBlank()) {
            return Duration.ZERO;
        }
        String trimmed = durationString.trim();
        // Try "hours:minutes" format (e.g. 0:00, 1:30, 00:45)
        if (trimmed.matches("\\d{1,2}:\\d{1,2}")) {
            String[] parts = trimmed.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return Duration.ofHours(hours).plusMinutes(minutes);
        }
        // Fallback to ISO-8601 (e.g. PT1H30M)
        try {
            return Duration.parse(trimmed);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse duration: " + durationString + ". Expected format: H:mm or HH:mm (e.g. 1:30) or ISO-8601 (e.g. PT1H30M)", e);
        }
    }

    public List<ImportRecord> parseTimeBookings(InputStream inputStream) {

        List<ImportRecord> result = new ArrayList<>();

        int rowNumber = 0;
        try (
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            CSVParser parser = CSVFormat.DEFAULT.builder()
                .setDelimiter(',')
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setSkipHeaderRecord(true)
                .setHeader() // first line = header, but we access by index
                .get()
                .parse(reader)
        ) {
            for (CSVRecord record : parser) {
                rowNumber++;
                // Expect 4 columns: gameName;date;duration;hours
                String gameName = record.get(0);
                LocalDate date = parseLocalDate(record.get(1));
                Duration duration = parseDuration(record.get(2));
                String hours = record.get(3);

                ParsedTimeBookingRow parsedRecord = new ParsedTimeBookingRow(
                    gameName,
                    date,
                    duration,
                    hours
                );

                result.add(new ImportRecord(UUID.randomUUID().toString(),null, rowNumber, toJson(parsedRecord)));
            }

        } catch (Exception e) {
            throw new IllegalStateException("CSV parse failed", e);
        }

        return result;
    }

    public List<ImportRecord> parseImportRecords(ImportType type,
            MultipartFile file) throws IOException {
        switch (type) {
            case TIME_BOOKING_MIGRATION:
                return parseTimeBookings(file.getInputStream());
            case GAME_INSTANCE_MIGRATION:
                return parseGameInstances(file.getInputStream());
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    private List<ImportRecord> parseGameInstances(InputStream inputStream) {
        List<ImportRecord> result = new ArrayList<>();
        int rowNumber = 0;
        try (
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            CSVParser parser = CSVFormat.DEFAULT.builder()
                .setDelimiter(',')
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setSkipHeaderRecord(true)
                .setHeader() // first line = header, but we access by index
                .get()
                .parse(reader)
        ) {
            // GameName,Platform,Backlog,Started,Finished,FinishedPlus,ReleaseDate,ReceivedDate,BacklogRemoved,BacklogAdded,FinishedOn,Free
            for (CSVRecord record : parser) {
                rowNumber++;
                // Expect 4 columns: gameName;date;duration;hours
                String gameName = record.get(0);
                String platform = record.get(1);
                boolean backlog = parseGDocCheckMarkboolean(record.get(2));
                boolean started = parseGDocCheckMarkboolean(record.get(3));
                boolean finished = parseGDocCheckMarkboolean(record.get(4));
                boolean finishedPlus = parseGDocCheckMarkboolean(record.get(5));
                LocalDate releaseDate = parseLocalDate(record.get(6));
                LocalDate receivedDate = parseLocalDate(record.get(7));
                LocalDate backlogRemoved = parseLocalDate(record.get(8));
                LocalDate backlogAdded = parseLocalDate(record.get(9));
                LocalDate finishedOn = parseLocalDate(record.get(10));
                boolean free = parseGDocCheckMarkboolean(record.get(11));

                ParsedGameInstanceRow parsedRecord = new ParsedGameInstanceRow(
                    gameName,
                    platform,
                    backlog,
                    started,
                    finished,
                    finishedPlus,
                    releaseDate,
                    receivedDate,
                    backlogRemoved,
                    backlogAdded,
                    finishedOn,
                    free
                );

                result.add(new ImportRecord(UUID.randomUUID().toString(),null, rowNumber, toJson(parsedRecord)));
            }
        } catch (Exception e) {
            throw new IllegalStateException("CSV parse failed", e);
        }
        return result;
    }

    private boolean parseGDocCheckMarkboolean(String value){
        if (value.equals("x")) {
            return true;
        } else if (value.equals("")) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid value: " + value);
        }
    }

    private String toJson(Object value) throws Exception {
        return value == null ? null : objectMapper.writeValueAsString(value);
    }
}