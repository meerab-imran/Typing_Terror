package dsa;

public class MyStack<T> {
    private T[] stack;
    private int top = -1;
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public MyStack() {
        stack = (T[]) new Object[DEFAULT_CAPACITY];
    }

    public void push(T item) {
        if (top == stack.length - 1) resize();
        stack[++top] = item;
    }

    public T pop() {
        if (isEmpty()) return null;
        T item = stack[top];
        stack[top--] = null;
        return item;
    }

    public T peek() {
        if (isEmpty()) return null;
        return stack[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    public void clear() {
        for (int i = 0; i <= top; i++) stack[i] = null;
        top = -1;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newStack = (T[]) new Object[stack.length * 2];
        for (int i = 0; i < stack.length; i++) newStack[i] = stack[i];
        stack = newStack;
    }
}
