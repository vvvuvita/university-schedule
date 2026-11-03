
package com.example.schedule.structure;
import java.util.List;
public class AVLTree<K extends Comparable<? super K>, T> implements EntityStore<K, T> {

    private AVLNode<K, T> root;
    private int size = 0;

    public AVLNode<K, T> getRoot() {
        return root;
    }

    @Override
    public synchronized int size() {
        return size;
    }

    @Override
    public synchronized boolean isEmpty() {
        return root == null;
    }

    // --- ВСТАВКА ---
    @Override
    public synchronized void add(K key, T value) {
        root = insert(root, key, value);
        size++;
    }

    private AVLNode<K, T> insert(AVLNode<K, T> node, K key, T value) {
        if (node == null) {
            return new AVLNode<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }

        updateHeight(node);
        return rebalance(node);
    }

    // --- ПОИСК ---
    @Override
    public synchronized T findByKey(K key) {
        AVLNode<K, T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.value;
            current = (cmp < 0) ? current.left : current.right;
        }
        return null;
    }

    // --- УДАЛЕНИЕ ---
    @Override
    public synchronized boolean removeByKey(K key) {
        int oldSize = size;
        root = remove(root, key);
        return size < oldSize;
    }

    //запоминаем путь
    public T findNearestLessonWithPath(java.time.LocalDateTime from, String targetGroup, List<AVLNode<K, T>> visitedPath) {
        return findNearestRecursive(root, from, targetGroup, visitedPath);
    }

    private T findNearestRecursive(AVLNode<K, T> node, java.time.LocalDateTime from, String targetGroup, List<AVLNode<K, T>> visitedPath) {
        if (node == null) return null;

        visitedPath.add(node);

        java.time.LocalDateTime nodeTime = (java.time.LocalDateTime) node.key;
        com.example.schedule.model.Lesson lesson = (com.example.schedule.model.Lesson) node.value;

        if (nodeTime.isBefore(from)) {
            return findNearestRecursive(node.right, from, targetGroup, visitedPath);
        }

        T leftResult = findNearestRecursive(node.left, from, targetGroup, visitedPath);
        if (leftResult != null) {
            return leftResult;
        }

        if (lesson.getGroup().equals(targetGroup)) {
            return node.value;
        }

        return findNearestRecursive(node.right, from, targetGroup, visitedPath);
    }
    private AVLNode<K, T> remove(AVLNode<K, T> node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            size--;
            if (node.left == null || node.right == null) {
                AVLNode<K, T> temp = (node.left != null) ? node.left : node.right;
                if (temp == null) node = null;
                else node = temp;
            } else {
                AVLNode<K, T> temp = minValueNode(node.right);
                node.key = temp.key;
                node.value = temp.value;
                node.right = remove(node.right, temp.key);
            }
        }

        if (node == null) return null;

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

    // --- БАЛАНСИРОВКА ---
    private int height(AVLNode<K, T> node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(AVLNode<K, T> node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    private int balanceFactor(AVLNode<K, T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private AVLNode<K, T> rebalance(AVLNode<K, T> node) {
        int balance = balanceFactor(node);

        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private AVLNode<K, T> rotateRight(AVLNode<K, T> y) {
        AVLNode<K, T> x = y.left;
        AVLNode<K, T> t2 = x.right;
        x.right = y;
        y.left = t2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

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