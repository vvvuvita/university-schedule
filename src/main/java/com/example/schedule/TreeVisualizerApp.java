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
    private Canvas canvas = new Canvas(1200, 600);
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private Random random = new Random();

    private ProgressBar progressBar = new ProgressBar(0);
    private Button btnCancel = new Button("Отмена");
    private Task<Void> generationTask;

    // Список для подсветки узлов
    private List<AVLNode<LocalDateTime, Lesson>> highlightedNodes = new ArrayList<>();

    private TextField manualTimeField = new TextField("2026-10-01T15:00");
    private TextField manualGroupField = new TextField("Группа-1");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Генерация
        Button btnGenerate = new Button("Сгенерировать 100 занятий");
        Button btnClear = new Button("Очистить");

        btnCancel.setDisable(true);
        btnCancel.setOnAction(e -> {
            if (generationTask != null) generationTask.cancel();
        });

        btnGenerate.setOnAction(e -> startGeneration());
        btnClear.setOnAction(e -> {
            tree = new AVLTree<>();
            highlightedNodes.clear();
            redraw();
        });

        HBox topPanel = new HBox(10, btnGenerate, btnCancel, btnClear, progressBar);
        topPanel.setPadding(new Insets(10));

        // ручное управление
        Button btnAdd = new Button("Вставить");
        Button btnRemove = new Button("Удалить");
        Button btnSearchExact = new Button("Найти (точное)");

        // кнопка ВСТАВИТЬ
        btnAdd.setOnAction(e -> {
            try {
                LocalDateTime time = LocalDateTime.parse(manualTimeField.getText());
                String group = manualGroupField.getText();
                Lesson lesson = new Lesson(time, group, "Ручной ввод", "Ауд-1", "Иванов");

                tree.add(time, lesson);
                highlightedNodes.clear();
                redraw();
                System.out.println("Добавлено: " + time + " для " + group);
            } catch (Exception ex) {
                System.out.println("Ошибка формата времени! Нужно: 2026-10-01T15:00");
            }
        });

        // кнопка УДАЛИТЬ
        btnRemove.setOnAction(e -> {
            try {
                LocalDateTime time = LocalDateTime.parse(manualTimeField.getText());
                boolean removed = tree.removeByKey(time);
                highlightedNodes.clear();
                redraw();
                System.out.println(removed ? "Удалено: " + time : "Не найдено: " + time);
            } catch (Exception ex) {
                System.out.println("Ошибка формата времени!");
            }
        });

        //кнопка ТОЧНЫЙ ПОИСК
        btnSearchExact.setOnAction(e -> {
            try {
                LocalDateTime time = LocalDateTime.parse(manualTimeField.getText());
                Lesson found = tree.findByKey(time);
                highlightedNodes.clear();

                if (found != null) {
                    System.out.println("Найдено: " + found.getGroup());
                    AVLNode<LocalDateTime, Lesson> nodeToHighlight = findNodeInSnapshot(tree.getRoot(), time);
                    if (nodeToHighlight != null) {
                        highlightedNodes.add(nodeToHighlight);
                    }
                } else {
                    System.out.println("Занятие не найдено.");
                }
                redraw();
            } catch (Exception ex) {
                System.out.println("Ошибка формата времени!");
            }
        });

        HBox manualPanel = new HBox(10,
                new Label("Время:"), manualTimeField,
                new Label("Группа:"), manualGroupField,
                btnAdd, btnRemove, btnSearchExact);
        manualPanel.setPadding(new Insets(10));
        manualPanel.setStyle("-fx-background-color: #e0f7fa; -fx-border-color: #b2ebf2;");

        // Поиск по группе
        Button btnFindNearest = new Button("Ближайшее для группы");
        TextField searchGroupField = new TextField("Группа-2");
        TextField searchTimeField = new TextField("2026-10-01T12:00");

        btnFindNearest.setOnAction(e -> {
            highlightedNodes.clear();
            try {
                LocalDateTime time = LocalDateTime.parse(searchTimeField.getText());
                String group = searchGroupField.getText();

                Lesson found = tree.findNearestLessonWithPath(time, group, highlightedNodes);

                if (found != null) {
                    System.out.println("Ближайшее: " + found);
                } else {
                    System.out.println("Ничего не найдено");
                }
                redraw();
            } catch (Exception ex) {
                System.out.println("Ошибка ввода времени. Формат: 2026-10-01T12:00");
            }
        });

        HBox searchPanel = new HBox(10, new Label("Группа:"), searchGroupField,
                new Label("Время (ISO):"), searchTimeField, btnFindNearest);
        searchPanel.setPadding(new Insets(10));



        VBox topContainer = new VBox(topPanel, manualPanel, searchPanel);
        root.setTop(topContainer);
        root.setCenter(canvas);

        redraw();

        Scene scene = new Scene(root);
        stage.setTitle("Визуализация AVL-дерева (Лабораторная №2)");
        stage.setScene(scene);
        stage.show();
    }

    //ассинхронная генерация
    private void startGeneration() {
        generationTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int total = 100;
                for (int i = 0; i < total; i++) {
                    if (isCancelled()) break;

                    LocalDateTime time = LocalDateTime.of(2026, 10, 1, 8, 0).plusHours(i);
                    Lesson lesson = new Lesson(time, "Группа-" + (i % 5), "Предмет", "Ауд", "Препод");

                    tree.add(time, lesson);
                    updateProgress(i + 1, total);
                    Thread.sleep(20); // Задержка для красоты
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(generationTask.progressProperty());

        generationTask.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(1.0);
            btnCancel.setDisable(true);
            highlightedNodes.clear();
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

    //отрисовка
    private void redraw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawNode(tree.getRoot(), canvas.getWidth() / 2, 60, 300);
    }

    private void drawNode(AVLNode<LocalDateTime, Lesson> node, double x, double y, double xOffset) {
        if (node == null) return;
        double radius = 25;

        //линии
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);
        if (node.left != null) {
            gc.strokeLine(x, y, x - xOffset, y + 70);
            drawNode(node.left, x - xOffset, y + 70, xOffset / 2);
        }
        if (node.right != null) {
            gc.strokeLine(x, y, x + xOffset, y + 70);
            drawNode(node.right, x + xOffset, y + 70, xOffset / 2);
        }

        //проверка на золото
        boolean isVisited = false;
        for (AVLNode<LocalDateTime, Lesson> visitedNode : highlightedNodes) {
            if (visitedNode.key.equals(node.key)) {
                isVisited = true;
                break;
            }
        }

        //узел
        if (isVisited) {
            gc.setFill(Color.GOLD); // Найденный
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(3);
        } else {
            gc.setFill(Color.LIGHTBLUE); // Обычный
            gc.setStroke(Color.DARKBLUE);
            gc.setLineWidth(2);
        }
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        //текст
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        String timeStr = node.key.getHour() + ":" + String.format("%02d", node.key.getMinute());
        gc.fillText(timeStr, x, y + 5);

        //высота узла
        gc.setFill(Color.GRAY);
        gc.setFont(Font.font(10));
        gc.fillText("h:" + node.height, x, y - radius - 5);
        gc.setFont(Font.font(14)); // Сброс шрифта
    }

    private AVLNode<LocalDateTime, Lesson> findNodeInSnapshot(AVLNode<LocalDateTime, Lesson> node, LocalDateTime time) {
        if (node == null) return null;
        int cmp = time.compareTo(node.key);
        if (cmp == 0) return node;
        return cmp < 0
                ? findNodeInSnapshot(node.left, time)
                : findNodeInSnapshot(node.right, time);
    }

    public static void main(String[] args) {
        launch(args);
    }
}