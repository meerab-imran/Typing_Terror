package dsa;

public class MyTree<T extends Comparable<T>> {
    private TreeNode root;
    private int size = 0;

    private class TreeNode {
        T data;
        TreeNode left, right;

        TreeNode(T data) {
            this.data = data;
        }
    }

    public void insert(T value) {
        if (value == null) return;
        root = insertRec(root, value);
        size++;
    }

    private TreeNode insertRec(TreeNode node, T value) {
        if (node == null) return new TreeNode(value);
        int cmp = value.compareTo(node.data);
        if (cmp < 0) node.left = insertRec(node.left, value);
        else if (cmp > 0) node.right = insertRec(node.right, value);
        return node;
    }

    public boolean search(T value) {
        if (value == null) return false;
        return searchRec(root, value);
    }

    private boolean searchRec(TreeNode node, T value) {
        if (node == null) return false;
        int cmp = value.compareTo(node.data);
        if (cmp == 0) return true;
        if (cmp < 0) return searchRec(node.left, value);
        return searchRec(node.right, value);
    }

    public MyArrayList<T> inOrderTraversal() {
        MyArrayList<T> result = new MyArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(TreeNode node, MyArrayList<T> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.data);
        inOrderRec(node.right, result);
    }

    public MyArrayList<T> preOrderTraversal() {
        MyArrayList<T> result = new MyArrayList<>();
        preOrderRec(root, result);
        return result;
    }

    private void preOrderRec(TreeNode node, MyArrayList<T> result) {
        if (node == null) return;
        result.add(node.data);
        preOrderRec(node.left, result);
        preOrderRec(node.right, result);
    }

    public MyArrayList<T> postOrderTraversal() {
        MyArrayList<T> result = new MyArrayList<>();
        postOrderRec(root, result);
        return result;
    }

    private void postOrderRec(TreeNode node, MyArrayList<T> result) {
        if (node == null) return;
        postOrderRec(node.left, result);
        postOrderRec(node.right, result);
        result.add(node.data);
    }

    public int getSize() {
        return size;
    }

    public T getMin() {
        if (root == null) return null;
        TreeNode node = root;
        while (node.left != null) node = node.left;
        return node.data;
    }

    public T getMax() {
        if (root == null) return null;
        TreeNode node = root;
        while (node.right != null) node = node.right;
        return node.data;
    }

    public boolean isEmpty() {
        return root == null;
    }
}
