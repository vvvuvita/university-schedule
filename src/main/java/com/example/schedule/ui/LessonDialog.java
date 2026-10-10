package com.example.schedule.ui;

import com.example.schedule.model.Lesson;
import com.example.schedule.model.Lecture;
import com.example.schedule.model.Practice;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Optional;

public class LessonDialog {

    private final Dialog<Lesson> dialog = new Dialog<>();
    private final TextField dateTimeField = new TextField();
    private final TextField groupField = new TextField();
    private final TextField disciplineField = new TextField();
    private final TextField roomField = new TextField();
    private final TextField teacherField = new TextField();
    private final TextField extraField = new TextField();
    private final ComboBox<String> typeBox = new ComboBox<>();

    private final Lesson existing;

    public LessonDialog(Stage owner) {
        this(owner, null);
    }

    public LessonDialog(Stage owner, Lesson existing) {
        this.existing = existing;

        dialog.initOwner(owner);
        dialog.setTitle(existing == null ? "Add a class" : "Change the activity");
        dialog.setHeaderText(existing == null
                ? "Enter the details of the new class."
                : "Edit the class data.");

        typeBox.getItems().addAll("Lecture", "Practice");
        typeBox.setValue(existing instanceof Practice ? "Practice" : "Lecture");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;
        grid.add(new Label("Type:"), 0, row);
        grid.add(typeBox, 1, row++);

        grid.add(new Label("Date-time (ISO):"), 0, row);
        grid.add(dateTimeField, 1, row++);

        grid.add(new Label("Group:"), 0, row);
        grid.add(groupField, 1, row++);

        grid.add(new Label("Discipline:"), 0, row);
        grid.add(disciplineField, 1, row++);

        grid.add(new Label("Room:"), 0, row);
        grid.add(roomField, 1, row++);

        grid.add(new Label("Teacher:"), 0, row);
        grid.add(teacherField, 1, row++);

        grid.add(new Label("Stream / Subgroup:"), 0, row);
        grid.add(extraField, 1, row);

        dialog.getDialogPane().setContent(grid);

        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, cancelType);

        dialog.setResultConverter(button -> {
            if (button == okType) {
                return buildLessonFromFields();
            }
            return null;
        });

        // Заполнение полей при редактировании
        if (existing != null) {
            dateTimeField.setText(existing.getDateTime().toString());
            groupField.setText(existing.getGroup());
            disciplineField.setText(existing.getDiscipline());
            roomField.setText(existing.getRoom());
            teacherField.setText(existing.getTeacher());

            if (existing instanceof Practice practice) {
                extraField.setText(practice.getSubgroup());
            } else if (existing instanceof Lecture lecture) {
                extraField.setText(lecture.getStream());
            }
        }
    }

    private Lesson buildLessonFromFields() {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeField.getText().trim());
        String group = groupField.getText().trim();
        String discipline = disciplineField.getText().trim();
        String room = roomField.getText().trim();
        String teacher = teacherField.getText().trim();
        String extra = extraField.getText().trim();

        if ("Practice".equals(typeBox.getValue())) {
            return new Practice(dateTime, group, discipline, room, teacher, extra);
        } else {
            return new Lecture(dateTime, group, discipline, room, teacher, extra);
        }
    }

    public Optional<Lesson> showAndWait() {
        return dialog.showAndWait();
    }
}