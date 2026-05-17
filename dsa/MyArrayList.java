package dsa;

public class MyArrayList<T> {
    private T[] array;
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public MyArrayList() {
        array = (T[]) new Object[DEFAULT_CAPACITY];
    }

    public void add(T item) {
        if (size == array.length) resize();
        array[size++] = item;
    }

    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        return array[index];
    }

    public T remove(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        T item = array[index];
        for (int i = index; i < size - 1; i++) array[i] = array[i + 1];
        array[--size] = null;
        return item;
    }

    public boolean remove(T item) {
        int idx = indexOf(item);
        if (idx == -1) return false;
        remove(idx);
        return true;
    }

    public void set(int index, T item) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        array[index] = item;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        array = (T[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public boolean contains(T item) {
        return indexOf(item) != -1;
    }

    public int indexOf(T item) {
        for (int i = 0; i < size; i++) {
            if (item == null ? array[i] == null : item.equals(array[i])) return i;
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newArray = (T[]) new Object[array.length * 2];
        for (int i = 0; i < size; i++) newArray[i] = array[i];
        array = newArray;
    }
}
