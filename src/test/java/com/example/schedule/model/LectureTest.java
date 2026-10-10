package com.example.schedule.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LectureTest {
    //все поля заполнены
    @Test
    @DisplayName("all fields are filled")
    void validate_allFieldsFilled_returnsEmptyList() {
        Lecture lecture = new Lecture(
                LocalDateTime.of(2026, 9, 26, 12, 0),
                "AVT-552", "Program by Java", "7-302", "Shperling V.K.",
                "Stream-1"
        );

        List<String> errors = lecture.validate();

        assertTrue(errors.isEmpty());
    }

    //пустой stream
    @Test
    @DisplayName("empty stream")
    void validate_emptyStream_returnsErrorAboutStream() {
        Lecture lecture = new Lecture(
                LocalDateTime.of(2026, 9, 26, 12, 0),
                "AVT-552", "Program by Java", "7-302", "Shperling V.K.",
                null
        );

        List<String> errors = lecture.validate();

        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("stream")));
    }

    //пустая группа
    @Test
    @DisplayName("empty group")
    void validate_emptyGroup_returnsErrorAboutGroup() {
        Lecture lecture = new Lecture(
                LocalDateTime.of(2026, 9, 26, 12, 0),
                "", "Program by Java", "7-302", "Shperling V.K.",
                "Stream-1"
        );

        List<String> errors = lecture.validate();

        assertTrue(errors.stream().anyMatch(e -> e.contains("group")));
    }

    //toString содержит метку [Lecture] и stream"
    @Test
    @DisplayName("toString contains the label [Lecture] and stream.")
    void toString_containsLectureAndStream() {
        Lecture lecture = new Lecture(
                LocalDateTime.of(2026, 9, 26, 12, 0),
                "AVT-552", "Program by Java", "7-302", "Shperling V.K.",
                "Stream-1"
        );

        String s = lecture.toString();
        assertTrue(s.contains("[Lecture]"));
        assertTrue(s.contains("Stream-1"));
    }
}

