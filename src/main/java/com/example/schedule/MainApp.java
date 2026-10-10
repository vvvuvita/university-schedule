package com.example.schedule;

import com.example.schedule.io.CsvException;
import com.example.schedule.io.CsvLoader;
import com.example.schedule.io.CsvSaver;
import com.example.schedule.model.Editable;
import com.example.schedule.model.Lesson;
import com.example.schedule.ui.LessonDialog;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class MainApp extends Application {

    //общий список данных для таблицы.
    private final ObservableList<Lesson> data = FXCollections.observableArrayList();

    //ссылка на главное окно нужна для диалогов.
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        //таблица
        TableView<Lesson> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setFixedCellSize(30);

        //колонка «Тип»
        TableColumn<Lesson, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell -> {
            Lesson lesson = cell.getValue();
            String type = lesson.getClass().getSimpleName();
            return new SimpleStringProperty(type);
        });
        typeCol.setPrefWidth(110);

        //колонка «Дата-время»
        TableColumn<Lesson, String> dateTimeCol = new TableColumn<>("Date-time");
        dateTimeCol.setCellValueFactory(cell -> {
            Lesson lesson = cell.getValue();
            String value = lesson.getDateTime() == null
                    ? ""
                    : lesson.getDateTime().toString();
            return new SimpleStringProperty(value);
        });
        dateTimeCol.setPrefWidth(160);

        //колонка «Группа»
        TableColumn<Lesson, String> groupCol = new TableColumn<>("Group");
        groupCol.setCellValueFactory(new PropertyValueFactory<>("group"));
        groupCol.setPrefWidth(100);

        //колонка «Дисциплина»
        TableColumn<Lesson, String> disciplineCol = new TableColumn<>("Discipline");
        disciplineCol.setCellValueFactory(new PropertyValueFactory<>("discipline"));
        disciplineCol.setPrefWidth(220);

        //колонка «Аудитория»
        TableColumn<Lesson, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));
        roomCol.setPrefWidth(100);

        //колонка «Преподаватель»
        TableColumn<Lesson, String> teacherCol = new TableColumn<>("Teacher");
        teacherCol.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        teacherCol.setPrefWidth(180);

        //добавляем колонки
        table.getColumns().add(typeCol);
        table.getColumns().add(dateTimeCol);
        table.getColumns().add(groupCol);
        table.getColumns().add(disciplineCol);
        table.getColumns().add(roomCol);
        table.getColumns().add(teacherCol);

        //привязываем данные к таблице
        table.setItems(data);

        //кнопка «Загрузить из CSV»
        Button loadButton = new Button("Load from CSV");
        loadButton.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select a CSV file");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV files", "*.csv"));

            File file = chooser.showOpenDialog(primaryStage);
            if (file == null) {
                return;
            }

            try {
                CsvLoader loader = new CsvLoader();
                CsvLoader.LoadResult result = loader.load(file.toPath());

                data.setAll(result.loaded());

                if (!result.skipped().isEmpty()) {
                    showSkippedDialog(result);
                }
            } catch (IOException e) {
                showErrorDialog("The file could not be read:\n" + e.getMessage());
            }
        });

        //кнопка «Сохранить в CSV»
        Button saveButton = new Button("Save in CSV");
        saveButton.setOnAction(event -> {
            if (data.isEmpty()) {
                showErrorDialog("There’s nothing to save: the table is empty.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save the CSV file");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV files", "*.csv"));
            chooser.setInitialFileName("schedule.csv");

            File file = chooser.showSaveDialog(primaryStage);
            if (file == null) {
                return;
            }

            try {
                CsvSaver saver = new CsvSaver();
                saver.save(file.toPath(), data);
                showInfoDialog("Saved: " + file.getAbsolutePath());
            } catch (IOException e) {
                showErrorDialog("The file could not be saved:\n" + e.getMessage());
            }
        });

        //кнопка «Добавить»
        Button addButton = new Button("Add");
        addButton.setOnAction(event -> {
            LessonDialog dialog = new LessonDialog(primaryStage);
            try {
                Optional<Lesson> result = dialog.showAndWait();
                result.ifPresent(lesson -> data.add(lesson));
            } catch (DateTimeParseException e) {
                showErrorDialog("Invalid date and time format.\n" +
                        "An ISO format is expected, for example: 2026-09-26T12:00");
            }
        });

        //кнопка «Изменить»
        Button editButton = new Button("Edit");
        editButton.setDisable(true); // по умолчанию заблокирована

        //обновляет состояние кнопки
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    boolean canEdit = newSel instanceof Editable;
                    editButton.setDisable(!canEdit);
                });

        editButton.setOnAction(event -> {
            Lesson selected = table.getSelectionModel().getSelectedItem();
            if (!(selected instanceof Editable)) {
                return; // защита на всякий случай
            }

            LessonDialog dialog = new LessonDialog(primaryStage, selected);
            try {
                Optional<Lesson> result = dialog.showAndWait();
                result.ifPresent(updated -> {
                    int index = data.indexOf(selected);
                    data.set(index, updated); // заменяем старую строку
                });
            } catch (DateTimeParseException e) {
                showErrorDialog("Invalid date and time format.\n" +
                        "An ISO format is expected, for example: 2026-09-26T12:00");
            }
        });

        //панель сверху
        HBox topPanel = new HBox(10);
        topPanel.setPadding(new Insets(10));
        topPanel.getChildren().addAll(loadButton, saveButton, addButton, editButton);

        //корневой контейнер
        BorderPane root = new BorderPane();
        root.setTop(topPanel);
        root.setCenter(table);

        //сцена и окно
        Scene scene = new Scene(root, 1000, 600);

        //подключаем CSS (если файл существует)
        var cssUrl = getClass().getResource("/com/example/schedule/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle("Class schedule");
        stage.show();
    }

    //диалог ошибки
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    //диалог информации
    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    //диалог о пропущенных строках
    private void showSkippedDialog(CsvLoader.LoadResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Lines are missing: ")
                .append(result.skipped().size())
                .append("\n\n");
        for (CsvException e : result.skipped()) {
            sb.append(e.getMessage()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.WARNING, sb.toString());
        alert.setHeaderText("Some lines were omitted.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}