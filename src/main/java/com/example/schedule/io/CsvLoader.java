package com.example.schedule.io;

import com.example.schedule.model.CancelledLesson;
import com.example.schedule.model.Lesson;
import com.example.schedule.model.Lecture;
import com.example.schedule.model.Practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    private static final String SEPARATOR = ";";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public LoadResult load(Path file) throws IOException {
        List<Lesson> loaded = new ArrayList<>(); //список успешно разобранных занятий
        List<CsvException> skipped = new ArrayList<>(); //список ошибок,битые строки

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) { //конструкция открывает файл на чтение и автоматически закрывает
            reader.readLine(); // заголовок не используем

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                try {
                    loaded.add(parseLine(line, lineNumber));
                } catch (CsvException e) {
                    skipped.add(e); //не падаем при первой ошибке, а кладем битые строки сюда
                }
            }
        }

        return new LoadResult(loaded, skipped); //возвращаем два списочка
    }
//метод parseLine
    private Lesson parseLine(String line, int lineNumber) throws CsvException {
        String[] parts = line.split(SEPARATOR, -1);

        if (parts.length < 6) {
            throw new CsvException(CsvException.Code.WRONG_FIELD_COUNT, lineNumber,
                    "expected at least 6 fields, got " + parts.length);
        }

        String type = parts[0].trim();
        LocalDateTime dateTime = parseDate(parts[1], lineNumber);

        return switch (type) {
            case "LECTURE" -> new Lecture(dateTime, parts[2], parts[3], parts[4], parts[5], parts[6]);
            case "PRACTICE" -> new Practice(dateTime, parts[2], parts[3], parts[4], parts[5], parts[6]);
            case "CANCELLED" -> new CancelledLesson(
                    new Lesson(dateTime, parts[2], parts[3], parts[4], parts[5]),
                    parts[6],
                    parseDate(parts[7], lineNumber)
            );
            default -> throw new CsvException(CsvException.Code.UNKNOWN_TYPE, lineNumber, type);
        };
    }

    private LocalDateTime parseDate(String text, int lineNumber) throws CsvException {
        try {
            return LocalDateTime.parse(text.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new CsvException(CsvException.Code.BAD_DATE, lineNumber, text);
        }
    }

    public static class LoadResult {
        private final List<Lesson> loaded;
        private final List<CsvException> skipped;

        public LoadResult(List<Lesson> loaded, List<CsvException> skipped) {
            this.loaded = loaded;
            this.skipped = skipped;
        }

        public List<Lesson> loaded() {
            return loaded;
        }

        public List<CsvException> skipped() {
            return skipped;
        }
    }
}