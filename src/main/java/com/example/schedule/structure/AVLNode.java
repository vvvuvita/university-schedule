package com.example.schedule.structure;

public class AVLNode<K, T> {
    public K key;
    public T value;
    public AVLNode<K, T> left;
    public AVLNode<K, T> right;
    public int height;

    public AVLNode(K key, T value) {
        this.key = key;
        this.value = value;
        this.height = 1;
    }
}
