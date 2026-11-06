package com.example.schedule;

import com.example.schedule.model.Lesson;
import com.example.schedule.structure.AVLNode;
import com.example.schedule.structure.AVLTree;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreeVisualizerApp extends Application {

    private AVLTree<LocalDateTime, Lesson> tree = new AVLTree<>();
    private Canvas canvas = new Canvas(1000, 700);
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private Random random = new Random();

    private ProgressBar progressBar = new ProgressBar(0);
    private Button btnCancel = new Button("Отмена");
    private Task<Void> generationTask;

    // Для подсветки поиска
    private List<AVLNode<LocalDateTime, Lesson>> highlightedNodes = new ArrayList<>();
    private TextField groupField = new TextField("Группа-1");
    private TextField timeField = new TextField("2026-10-01T12:00");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        Button btnGenerate = new Button("Сгенерировать 100 занятий");
        Button btnClear = new Button("Очистить");
        Button btnFind = new Button("Найти ближайшее");

        btnCancel.setDisable(true);
        btnCancel.setOnAction(e -> { if (generationTask != null) generationTask.cancel(); });

        btnGenerate.setOnAction(e -> startGeneration());
        btnClear.setOnAction(e -> {
            tree = new AVLTree<>();
            highlightedNodes.clear();
            redraw();
        });

        btnFind.setOnAction(e -> {
            highlightedNodes.clear(); // Очищаем прошлую подсветку
            try {
                LocalDateTime time = LocalDateTime.parse(timeField.getText());
                String group = groupField.getText();

                Lesson found = tree.findNearestLessonWithPath(time, group, highlightedNodes);

                if (found != null) {
                    System.out.println("Найдено: " + found);
                } else {
                    System.out.println("Ничего не найдено");
                }
                redraw(); // Перерисовываем с подсветкой
            } catch (Exception ex) {
                System.out.println("Ошибка ввода времени. Формат: 2026-10-01T12:00");
            }
        });

        // Верхняя панель
        HBox topPanel = new HBox(10, btnGenerate, btnCancel, btnClear, progressBar);
        topPanel.setPadding(new Insets(10));

        // Панель поиска
        HBox searchPanel = new HBox(10, new Label("Группа:"), groupField,
                new Label("Время (ISO):"), timeField, btnFind);
        searchPanel.setPadding(new Insets(10));

        VBox topContainer = new VBox(topPanel, searchPanel);

        root.setTop(topContainer);
        root.setCenter(canvas);

        redraw();

        Scene scene = new Scene(root);
        stage.setTitle("Визуализация AVL-дерева (Расписание)");
        stage.setScene(scene);
        stage.show();
    }

    private void startGeneration() {
        generationTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int total = 100;
                for (int i = 0; i < total; i++) {
                    if (isCancelled()) break;
                    // Генерируем занятия с интервалом в 1 час
                    LocalDateTime time = LocalDateTime.of(2026, 10, 1, 8, 0).plusHours(i);
                    Lesson lesson = new Lesson(time, "Группа-" + (i % 5), "Предмет", "Ауд", "Препод");
                    tree.add(time, lesson);
                    updateProgress(i + 1, total);
                    Thread.sleep(30);
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(generationTask.progressProperty());
        generationTask.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(1.0);
            btnCancel.setDisable(true);
            redraw();
        });
        generationTask.setOnCancelled(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            btnCancel.setDisable(true);
            redraw();
        });

        btnCancel.setDisable(false);
        new Thread(generationTask).start();
    }

    private void redraw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(14));
        drawNode(tree.getRoot(), canvas.getWidth() / 2, 100, 250); // сдвинули Y вниз из-за новой панели
    }

    private void drawNode(AVLNode<LocalDateTime, Lesson> node, double x, double y, double xOffset) {
        if (node == null) return;
        double radius = 25;

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);
        if (node.left != null) {
            gc.strokeLine(x, y, x - xOffset, y + 80);
            drawNode(node.left, x - xOffset, y + 80, xOffset / 2);
        }
        if (node.right != null) {
            gc.strokeLine(x, y, x + xOffset, y + 80);
            drawNode(node.right, x + xOffset, y + 80, xOffset / 2);
        }

        // если узел в списке, красим в золотой
        boolean visited = false;

        for (AVLNode<LocalDateTime, Lesson> visitedNode : highlightedNodes) {
            if (visitedNode.key.equals(node.key)) {
                visited = true;
                break;
            }
        }

        if (visited) {
            gc.setFill(Color.GOLD);
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(3);
        } else {
            gc.setFill(Color.LIGHTBLUE);
            gc.setStroke(Color.DARKBLUE);
            gc.setLineWidth(2);
        }

        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        String timeStr = node.key.getHour() + ":" + String.format("%02d", node.key.getMinute());
        gc.fillText(timeStr, x, y + 5);

        gc.setFill(Color.GRAY);
        gc.setFont(Font.font(10));
        gc.fillText("h:" + node.height, x, y - radius - 5);
        gc.setFont(Font.font(14));
    }

    public static void main(String[] args) {
        launch(args);
    }
}