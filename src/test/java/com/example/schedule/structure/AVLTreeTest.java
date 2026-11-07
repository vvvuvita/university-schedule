package com.example.schedule.structure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    @Test
    void shouldInsertElements() {
        AVLTree<Integer, String> tree = new AVLTree<>();

        tree.add(10, "A");
        tree.add(20, "B");
        tree.add(30, "C");

        assertEquals(3, tree.size());
        assertEquals("A", tree.findByKey(10));
        assertEquals("B", tree.findByKey(20));
        assertEquals("C", tree.findByKey(30));
    }

    @Test
    void shouldNotIncreaseSizeWhenReplacingValue() {
        AVLTree<Integer, String> tree = new AVLTree<>();

        tree.add(10, "The first value");
        tree.add(10, "The new value");

        assertEquals(1, tree.size());
        assertEquals("The new value", tree.findByKey(10));
    }

    @Test
    void shouldRemoveElement() {
        AVLTree<Integer, String> tree = new AVLTree<>();

        tree.add(10, "A");
        tree.add(20, "B");
        tree.add(30, "C");

        assertTrue(tree.removeByKey(20));
        assertEquals(2, tree.size());
        assertNull(tree.findByKey(20));
    }

    @Test
    void shouldNotRemoveMissingElement() {
        AVLTree<Integer, String> tree = new AVLTree<>();

        tree.add(10, "A");

        assertFalse(tree.removeByKey(99));
        assertEquals(1, tree.size());
    }

    @Test
    void shouldCorrectlyRemoveNodeWithTwoChildren() {
        AVLTree<Integer, String> tree = new AVLTree<>();

        tree.add(20, "A");
        tree.add(10, "B");
        tree.add(30, "C");
        tree.add(25, "D");
        tree.add(40, "E");

        assertTrue(tree.removeByKey(20));

        assertEquals(4, tree.size());
        assertNull(tree.findByKey(20));
        assertEquals("D", tree.findByKey(25));
        assertEquals("C", tree.findByKey(30));
    }

    @Test
    void shouldKeepTreeBalancedAfterInsertions() {
        AVLTree<Integer, String> tree = new AVLTree<>();

        for (int i = 1; i <= 100; i++) {
            tree.add(i, "Value " + i);
        }

        assertEquals(100, tree.size());

        AVLNode<Integer, String> root = tree.getRoot();

        assertNotNull(root);
        assertTrue(isBalanced(root));
    }

    @Test
    void shouldReturnCopyOfRoot() {
        AVLTree<Integer, String> tree = new AVLTree<>();

        tree.add(10, "A");
        tree.add(5, "B");

        AVLNode<Integer, String> copy = tree.getRoot();
        copy.key = 999;

        assertNotEquals(999, tree.getRoot().key);
        assertEquals("A", tree.findByKey(10));
    }

    private boolean isBalanced(AVLNode<Integer, String> node) {
        if (node == null) {
            return true;
        }

        int leftHeight = actualHeight(node.left);
        int rightHeight = actualHeight(node.right);

        return Math.abs(leftHeight - rightHeight) <= 1
                && isBalanced(node.left)
                && isBalanced(node.right);
    }

    private int actualHeight(AVLNode<Integer, String> node) {
        if (node == null) {
            return 0;
        }

        return 1 + Math.max(
                actualHeight(node.left),
                actualHeight(node.right)
        );
    }
}

