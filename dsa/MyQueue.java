package dsa;

public class MyQueue<T> {
    private T[] queue;
    private int front = 0;
    private int rear = -1;
    private int currentSize = 0;
    private static final int CAPACITY = 100;

    @SuppressWarnings("unchecked")
    public MyQueue() {
        queue = (T[]) new Object[CAPACITY];
    }

    public boolean enqueue(T item) {
        if (isFull()) {
            System.out.println("Queue is full");
            return false;
        }
        rear = (rear + 1) % CAPACITY;
        queue[rear] = item;
        currentSize++;
        return true;
    }

    public T dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return null;
        }
        T item = queue[front];
        queue[front] = null;
        front = (front + 1) % CAPACITY;
        currentSize--;
        return item;
    }

    public T peek() {
        if (isEmpty()) return null;
        return queue[front];
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public boolean isFull() {
        return currentSize == CAPACITY;
    }

    public int size() {
        return currentSize;
    }

    public void clear() {
        for (int i = 0; i < CAPACITY; i++) queue[i] = null;
        front = 0;
        rear = -1;
        currentSize = 0;
    }
}
