package com.example.schedule;

import com.example.schedule.structure.AVLTree;
import java.util.Random;
import java.util.TreeMap;

public class Benchmark {
    public static void main(String[] args) {
        int[] sizes = {10_000, 100_000};

        System.out.println("COMPARISON OF PERFORMANCE"); //сравнение производительности
        System.out.println("(Time in milliseconds)\n");

        for (int size : sizes) {
            System.out.println("Data size: " + size + " ");

            int[] keys = new int[size];
            Random random = new Random(42);
            for (int i = 0; i < size; i++) {
                keys[i] = random.nextInt();
            }

            //вставка
            AVLTree<Integer, String> avl = new AVLTree<>();
            long start = System.nanoTime();
            for (int key : keys) avl.add(key, "value");
            long avlInsert = (System.nanoTime() - start) / 1_000_000;

            TreeMap<Integer, String> map = new TreeMap<>();
            start = System.nanoTime();
            for (int key : keys) map.put(key, "value");
            long mapInsert = (System.nanoTime() - start) / 1_000_000;

            //поиск
            start = System.nanoTime();
            for (int i = 0; i < 1000; i++) avl.findByKey(keys[i]);
            long avlSearch = (System.nanoTime() - start) / 1_000_000;

            start = System.nanoTime();
            for (int i = 0; i < 1000; i++) map.get(keys[i]);
            long mapSearch = (System.nanoTime() - start) / 1_000_000;

            //удаление
            start = System.nanoTime();
            for (int i = 0; i < 1000; i++) avl.removeByKey(keys[i]);
            long avlDelete = (System.nanoTime() - start) / 1_000_000;

            start = System.nanoTime();
            for (int i = 0; i < 1000; i++) map.remove(keys[i]);
            long mapDelete = (System.nanoTime() - start) / 1_000_000;

            // ВЫВОД
            System.out.printf("Add: AVL = %d мс | TreeMap = %d мс%n", avlInsert, mapInsert);
            System.out.printf("Find (1000): AVL = %d мс | TreeMap = %d мс%n", avlSearch, mapSearch);
            System.out.printf("Remove (1000): AVL = %d мс | TreeMap = %d мс%n", avlDelete, mapDelete);
            System.out.println();
        }
    }
}
