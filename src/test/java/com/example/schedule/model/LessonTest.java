package com.example.schedule.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LessonTest {
//конструктор с параметрами заполняет поля
    @Test
    @DisplayName("The constructor fills in all fields with parameters")
    void constructor_setsAllFields() {
        LocalDateTime dt = LocalDateTime.of(2026, 9, 26, 12, 0);

        Lesson lesson = new Lesson(dt, "AVT-552", "Program by Java", "7-302", "Shperling V.K.");

        assertEquals(dt, lesson.getDateTime());
        assertEquals("AVT-552", lesson.getGroup());
        assertEquals("Program by Java", lesson.getDiscipline());
        assertEquals("7-302", lesson.getRoom());
        assertEquals("Shperling V.K.", lesson.getTeacher());
        assertNotNull(lesson.getId());
    }
//пустой конструктор генерирует айди
    @Test
    @DisplayName("The parameterless constructor generates an id")
    void defaultConstructor_generatesId() {
        Lesson lesson = new Lesson();
        assertNotNull(lesson.getId());
    }
//два разных занятия имеют разные айди
    @Test
    @DisplayName("Two different classes have different id")
    void twoLessons_haveDifferentIds() {
        Lesson a = new Lesson();
        Lesson b = new Lesson();
        assertNotEquals(a.getId(), b.getId());
    }
//сеттер меняет группу
    @Test
    @DisplayName("The setGroup setter changes the group")
    void setGroup_changesValue() {
        Lesson lesson = new Lesson();
        lesson.setGroup("AVT-551");
        assertEquals("AVT-551", lesson.getGroup());
    }
//тустринг имеет группу и дисциплину
    @Test
    @DisplayName("toString contains a group and a discipline")
    void toString_containsGroupAndDiscipline() {
        LocalDateTime dt = LocalDateTime.of(2026, 9, 26, 12, 0);
        Lesson lesson = new Lesson(dt, "AVT-552", "Program by Java", "7-302", "Shperling V.K.");

        String s = lesson.toString();
        assertTrue(s.contains("AVT-552"));
        assertTrue(s.contains("Program by Java"));
    }
}