
package com.example.schedule.structure;
import com.example.schedule.model.Lesson;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class AVLTree<K extends Comparable<? super K>, T>
        implements EntityStore<K, T> {

    private AVLNode<K, T> root;
    private int size = 0;

    public synchronized AVLNode<K, T> getRoot() {
        return copyTree(root);
    }

    private AVLNode<K, T> copyTree(AVLNode<K, T> node) {
        if (node == null) {
            return null;
        }

        AVLNode<K, T> copy = new AVLNode<>(node.key, node.value);
        copy.height = node.height;
        copy.left = copyTree(node.left);
        copy.right = copyTree(node.right);

        return copy;
    }

    @Override
    public synchronized int size() {
        return size;
    }

    @Override
    public synchronized boolean isEmpty() {
        return root == null;
    }

    // Вставка
    @Override
    public synchronized void add(K key, T value) {
        Objects.requireNonNull(key, "Ключ не может быть null");

        boolean keyAlreadyExists = containsKey(root, key);

        root = insert(root, key, value);

        if (!keyAlreadyExists) {
            size++;
        }
    }

    private boolean containsKey(AVLNode<K, T> node, K key) {
        while (node != null) {
            int cmp = key.compareTo(node.key);

            if (cmp == 0) {
                return true;
            }

            node = cmp < 0 ? node.left : node.right;
        }

        return false;
    }

    private AVLNode<K, T> insert(
            AVLNode<K, T> node,
            K key,
            T value
    ) {
        if (node == null) {
            return new AVLNode<>(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            // Такой ключ уже есть — заменяем значение
            node.value = value;
            return node;
        }

        updateHeight(node);
        return rebalance(node);
    }

    // Поиск
    @Override
    public synchronized T findByKey(K key) {
        Objects.requireNonNull(key, "Ключ не может быть null");

        AVLNode<K, T> current = root;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp == 0) {
                return current.value;
            }

            current = cmp < 0 ? current.left : current.right;
        }

        return null;
    }

    // Удаление
    @Override
    public synchronized boolean removeByKey(K key) {
        Objects.requireNonNull(key, "Ключ не может быть null");

        if (!containsKey(root, key)) {
            return false;
        }

        root = remove(root, key);
        size--;

        return true;
    }

    private AVLNode<K, T> remove(AVLNode<K, T> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            // Узел найден.
            if (node.left == null) {
                return node.right;
            }

            if (node.right == null) {
                return node.left;
            }

            // Есть два потомка: заменяем узел его преемником
            AVLNode<K, T> successor = minValueNode(node.right);

            node.key = successor.key;
            node.value = successor.value;

            // Удаляем преемника без изменения size.
            node.right = remove(node.right, successor.key);
        }

        updateHeight(node);
        return rebalance(node);
    }

    private AVLNode<K, T> minValueNode(AVLNode<K, T> node) {
        AVLNode<K, T> current = node;

        while (current.left != null) {
            current = current.left;
        }

        return current;
    }

    public synchronized T findNearestLessonWithPath(
            LocalDateTime from,
            String targetGroup,
            List<AVLNode<K, T>> visitedPath
    ) {
        Objects.requireNonNull(from, "Дата и время не могут быть null");
        Objects.requireNonNull(targetGroup, "Группа не может быть null");
        Objects.requireNonNull(visitedPath, "Список пути не может быть null");

        return findNearestRecursive(root, from, targetGroup, visitedPath);
    }

    private T findNearestRecursive(
            AVLNode<K, T> node,
            LocalDateTime from,
            String targetGroup,
            List<AVLNode<K, T>> visitedPath
    ) {
        if (node == null) {
            return null;
        }

        visitedPath.add(node);

        // Проверяем типы явно
        Object keyObject = node.key;
        Object valueObject = node.value;

        if (!(keyObject instanceof LocalDateTime nodeTime)
                || !(valueObject instanceof Lesson lesson)) {
            throw new IllegalStateException(
                    "Поиск ближайшего занятия требует ключ LocalDateTime "
                            + "и значение Lesson"
            );
        }

        // Если время узла уже прошло, слева подходящих занятий нет
        if (nodeTime.isBefore(from)) {
            return findNearestRecursive(
                    node.right, from, targetGroup, visitedPath
            );
        }

        // Сначала ищем более раннее подходящее занятие слева
        T leftResult = findNearestRecursive(
                node.left, from, targetGroup, visitedPath
        );

        if (leftResult != null) {
            return leftResult;
        }

        // Затем проверяем текущий узел
        if (Objects.equals(lesson.getGroup(), targetGroup)) {
            return node.value;
        }

        // И только потом ищем справа
        return findNearestRecursive(
                node.right, from, targetGroup, visitedPath
        );
    }

    // Балансировка

    private int height(AVLNode<K, T> node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(AVLNode<K, T> node) {
        if (node != null) {
            node.height = 1 + Math.max(
                    height(node.left),
                    height(node.right)
            );
        }
    }

    private int balanceFactor(AVLNode<K, T> node) {
        return node == null
                ? 0
                : height(node.left) - height(node.right);
    }

    private AVLNode<K, T> rebalance(AVLNode<K, T> node) {
        int balance = balanceFactor(node);

        // Перевес слева
        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }

            return rotateRight(node);
        }

        // Перевес справа
        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }

            return rotateLeft(node);
        }

        return node;
    }

    // Правый поворот
    private AVLNode<K, T> rotateRight(AVLNode<K, T> y) {
        AVLNode<K, T> x = y.left;
        AVLNode<K, T> t2 = x.right;

        x.right = y;
        y.left = t2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Левый поворот
    private AVLNode<K, T> rotateLeft(AVLNode<K, T> x) {
        AVLNode<K, T> y = x.right;
        AVLNode<K, T> t2 = y.left;

        y.left = x;
        x.right = t2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }
}
