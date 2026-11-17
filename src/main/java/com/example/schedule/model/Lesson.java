package com.example.schedule.model;

import java.time.LocalDateTime;
import java.util.UUID;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Lecture.class, name = "lecture"),
        @JsonSubTypes.Type(value = Practice.class, name = "practice"),
        @JsonSubTypes.Type(value = CancelledLesson.class, name = "cancelled")
})
public class Lesson implements Serializable {
    private static final long serialVersionUID = 1L;


        protected UUID id;
        protected LocalDateTime dateTime;
        protected String group;
        protected String discipline;
        protected String room;
        protected String teacher;

        public Lesson() {             //конструктор по умолчанию
            this.id = UUID.randomUUID();
        }

        public Lesson(LocalDateTime dateTime, String group, String discipline,
                      String room, String teacher) {  //конструктор с параметрами
            this(); //вызов прошлого конструктора для создания id
            this.dateTime = dateTime;
            this.group = group;
            this.discipline = discipline;
            this.room = room;
            this.teacher = teacher;
        }

        public UUID getId() {  //геттеры
            return id;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public String getGroup() {
            return group;
        }

        public String getDiscipline() {
            return discipline;
        }

        public String getRoom() {
            return room;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setDateTime(LocalDateTime dateTime) { //сеттеры
            this.dateTime = dateTime;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public void setDiscipline(String discipline) {
            this.discipline = discipline;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        @Override
        public String toString() {
            return "Lesson{" +
                    "date-time=" + dateTime +
                    ", group=" + group +
                    ", discipline=" + discipline +
                    ", room=" + room +
                    ", teacher=" + teacher +
                    '}';
        }
    }

