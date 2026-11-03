package com.example.schedule.structure;

public interface EntityStore<K extends Comparable<? super K>, T> {
    void add(K key, T entity);
    T findByKey(K key);
    boolean removeByKey(K key);
    int size();
    boolean isEmpty();
}