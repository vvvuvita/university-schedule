package com.example.schedule.io;

import com.example.schedule.model.CancelledLesson;
import com.example.schedule.model.Lesson;
import com.example.schedule.model.Lecture;
import com.example.schedule.model.Practice;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvSaver {

    private static final String SEPARATOR = ";";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void save(Path file, List<Lesson> lessons) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("type;dateTime;group;discipline;room;teacher;extra1;extra2");
            writer.newLine();

            for (Lesson lesson : lessons) {
                writer.write(toCsv(lesson));
                writer.newLine();
            }
        }
    }

    private String toCsv(Lesson lesson) {
        if (lesson instanceof CancelledLesson cancelled) {
            return "CANCELLED" + SEPARATOR +
                    cancelled.getDateTime() + SEPARATOR +
                    cancelled.getGroup() + SEPARATOR +
                    cancelled.getDiscipline() + SEPARATOR +
                    cancelled.getRoom() + SEPARATOR +
                    cancelled.getTeacher() + SEPARATOR +
                    cancelled.getReason() + SEPARATOR +
                    cancelled.getCancelledAt();
        }
        if (lesson instanceof Lecture lecture) {
            return "LECTURE" + SEPARATOR +
                    lecture.getDateTime() + SEPARATOR +
                    lecture.getGroup() + SEPARATOR +
                    lecture.getDiscipline() + SEPARATOR +
                    lecture.getRoom() + SEPARATOR +
                    lecture.getTeacher() + SEPARATOR +
                    lecture.getStream() + SEPARATOR;
        }
        if (lesson instanceof Practice practice) {
            return "PRACTICE" + SEPARATOR +
                    practice.getDateTime() + SEPARATOR +
                    practice.getGroup() + SEPARATOR +
                    practice.getDiscipline() + SEPARATOR +
                    practice.getRoom() + SEPARATOR +
                    practice.getTeacher() + SEPARATOR +
                    practice.getSubgroup() + SEPARATOR;
        }
        return "LESSON" + SEPARATOR +
                lesson.getDateTime() + SEPARATOR +
                lesson.getGroup() + SEPARATOR +
                lesson.getDiscipline() + SEPARATOR +
                lesson.getRoom() + SEPARATOR +
                lesson.getTeacher() + SEPARATOR +
                SEPARATOR;
    }
}
