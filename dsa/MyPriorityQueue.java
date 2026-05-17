package dsa;

public class MyPriorityQueue<T extends Comparable<T>> {
    private T[] heap;
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public MyPriorityQueue() {
        heap = (T[]) new Comparable[DEFAULT_CAPACITY];
    }

    public void insert(T item) {
        if (size == heap.length) resize();
        heap[size] = item;
        heapifyUp(size);
        size++;
    }

    public T extractMax() {
        if (isEmpty()) return null;
        T max = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) heapifyDown(0);
        return max;
    }

    public T peekMax() {
        if (isEmpty()) return null;
        return heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index].compareTo(heap[parent]) > 0) {
                T temp = heap[index];
                heap[index] = heap[parent];
                heap[parent] = temp;
                index = parent;
            } else break;
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int largest = index;
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            if (left < size && heap[left].compareTo(heap[largest]) > 0) largest = left;
            if (right < size && heap[right].compareTo(heap[largest]) > 0) largest = right;
            if (largest != index) {
                T temp = heap[index];
                heap[index] = heap[largest];
                heap[largest] = temp;
                index = largest;
            } else break;
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newHeap = (T[]) new Comparable[heap.length * 2];
        for (int i = 0; i < size; i++) newHeap[i] = heap[i];
        heap = newHeap;
    }

    public void clear() {
        for (int i = 0; i < size; i++) heap[i] = null;
        size = 0;
    }
}
