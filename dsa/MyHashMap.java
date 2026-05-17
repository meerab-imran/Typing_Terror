package dsa;

import models.WordInfo;

public class MyHashMap {
    private HashNode[] buckets;
    private int bucketCount = 50;
    private int currentSize = 0;

    private static class HashNode {
        String key;
        WordInfo value;
        HashNode next;

        HashNode(String key, WordInfo value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    public MyHashMap() {
        buckets = new HashNode[bucketCount];
    }

    private int getIndex(String key) {
        return Math.abs(key.hashCode()) % bucketCount;
    }

    public void put(String key, WordInfo value) {
        if (key == null) return;
        int index = getIndex(key);
        HashNode node = buckets[index];
        while (node != null) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
            node = node.next;
        }
        HashNode newNode = new HashNode(key, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
        currentSize++;
    }

    public WordInfo get(String key) {
        if (key == null) return null;
        int index = getIndex(key);
        HashNode node = buckets[index];
        while (node != null) {
            if (node.key.equals(key)) return node.value;
            node = node.next;
        }
        return null;
    }

    public boolean containsKey(String key) {
        return get(key) != null;
    }

    public WordInfo remove(String key) {
        if (key == null) return null;
        int index = getIndex(key);
        HashNode node = buckets[index];
        HashNode prev = null;
        while (node != null) {
            if (node.key.equals(key)) {
                if (prev == null) buckets[index] = node.next;
                else prev.next = node.next;
                currentSize--;
                return node.value;
            }
            prev = node;
            node = node.next;
        }
        return null;
    }

    public int size() {
        return currentSize;
    }

    public MyArrayList<String> keySet() {
        MyArrayList<String> keys = new MyArrayList<>();
        for (int i = 0; i < bucketCount; i++) {
            HashNode node = buckets[i];
            while (node != null) {
                keys.add(node.key);
                node = node.next;
            }
        }
        return keys;
    }

    public void clear() {
        for (int i = 0; i < bucketCount; i++) buckets[i] = null;
        currentSize = 0;
    }
}
