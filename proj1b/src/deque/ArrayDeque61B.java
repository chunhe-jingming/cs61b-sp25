package deque;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A deque implemented with a circular backing array.
 *  @param <T> the type of elements held in this deque.
 */
public class ArrayDeque61B<T> implements Deque61B<T>, Iterable<T> {

    /** Backing array that stores the deque items in circular fashion. */
    private Object[] items;
    /** Index just before the first item of the deque. */
    private int nextFirst;
    /** Index just after the last item of the deque. */
    private int nextLast;
    /** Number of items in the deque. */
    private int size;

    /** Creates an empty deque. */
    public ArrayDeque61B() {
        items = new Object[8];
        nextFirst = 0;
        nextLast = 1;
        size = 0;
    }

    /** Add {@code x} to the front of the deque. Assumes {@code x} is never null.
     *
     * @param x item to add
     */
    @Override
    public void addFirst(T x) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = x;
        nextFirst = Math.floorMod(nextFirst - 1, items.length);
        size++;
    }

    /** Add {@code x} to the back of the deque. Assumes {@code x} is never null.
     *
     * @param x item to add
     */
    @Override
    public void addLast(T x) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = x;
        nextLast = Math.floorMod(nextLast + 1, items.length);
        size++;
    }

    /** Returns a List copy of the deque. Does not alter the deque.
     *
     * @return a new list copy of the deque.
     */
    @Override
    public List<T> toList() {
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(get(i));
        }
        return result;
    }

    /** Returns if the deque is empty. Does not alter the deque.
     *
     * @return {@code true} if the deque has no elements, {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the size of the deque. Does not alter the deque.
     *
     * @return the number of items in the deque.
     */
    @Override
    public int size() {
        return size;
    }

    /** Remove and return the element at the front of the deque, if it exists.
     *
     * @return removed element, otherwise {@code null}.
     */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst = Math.floorMod(nextFirst + 1, items.length);
        T removed = (T) items[nextFirst];
        items[nextFirst] = null;
        size--;
        return removed;
    }

    /** Remove and return the element at the back of the deque, if it exists.
     *
     * @return removed element, otherwise {@code null}.
     */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast = Math.floorMod(nextLast - 1, items.length);
        T removed = (T) items[nextLast];
        items[nextLast] = null;
        size--;
        return removed;
    }

    /** The Deque61B abstract data type does not typically have a get method,
     * but we've included this extra operation to provide you with some
     * extra programming practice. Gets the element, iteratively. Returns
     * null if index is out of bounds. Does not alter the deque.
     *
     * @param index index to get
     * @return element at {@code index} in the deque
     */
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return (T) items[Math.floorMod(nextFirst + 1 + index, items.length)];
    }

    /** This method technically shouldn't be in the interface, but it's here
     * to make testing nice. Gets an element, recursively. Returns null if
     * index is out of bounds. Does not alter the deque.
     *
     * @param index index to get
     * @return element at {@code index} in the deque
     */
    @Override
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(nextFirst + 1, index);
    }

    /** Recursively returns the item at {@code index} positions after the
     *  array position {@code startIndex}, wrapping around the array. */
    private T getRecursiveHelper(int startIndex, int index) {
        if (index == 0) {
            return (T) items[Math.floorMod(startIndex, items.length)];
        }
        return getRecursiveHelper(startIndex + 1, index - 1);
    }

    /** Doubles the capacity of the backing array and relayouts the items
     *  contiguously starting at index 0. */
    private void resize(int capacity) {
        Object[] newItems = new Object[capacity];
        for (int i = 0; i < size; i++) {
            newItems[i] = get(i);
        }
        items = newItems;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    /** Returns an iterator over the items from first to last. */
    @Override
    public Iterator<T> iterator() {
        return new ArrayDeque61BIterator();
    }

    /** Iterates over the deque from front to back. */
    private class ArrayDeque61BIterator implements Iterator<T> {
        private int position;

        ArrayDeque61BIterator() {
            position = 0;
        }

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public T next() {
            T item = get(position);
            position++;
            return item;
        }
    }
}
