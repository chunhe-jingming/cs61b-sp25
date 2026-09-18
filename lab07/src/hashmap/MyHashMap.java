package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *  A hash table-backed Map implementation.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author chunhe-jingming
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /** Default initial number of buckets. */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /** Default maximum load factor. */
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    /** Factor by which the backing array grows on resize. */
    private static final int RESIZE_FACTOR = 2;

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private final double loadFactor;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialCapacity.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialCapacity initial size of backing array
     * @param loadFactor maximum load factor
     */
    public MyHashMap(int initialCapacity, double loadFactor) {
        this.loadFactor = loadFactor;
        buckets = new Collection[initialCapacity];
        for (int i = 0; i < initialCapacity; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *  Note that that this is referring to the hash table bucket itself,
     *  not the hash map itself.
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /** Returns the index of the bucket that should hold key, using
     *  floorMod so that negative hash codes map to valid indices. */
    private int bucketIndex(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }

    /** Returns the node in bucket whose key equals key (via equals),
     *  or null if there is no such node. */
    private Node findNode(Collection<Node> bucket, K key) {
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        Collection<Node> bucket = buckets[bucketIndex(key)];
        Node existing = findNode(bucket, key);
        if (existing != null) {
            existing.value = value;
            return;
        }
        bucket.add(new Node(key, value));
        size++;
        if ((double) size / buckets.length > loadFactor) {
            resize();
        }
    }

    /** Doubles the number of buckets and rehashes every node. */
    private void resize() {
        Collection<Node>[] oldBuckets = buckets;
        buckets = new Collection[oldBuckets.length * RESIZE_FACTOR];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        for (Collection<Node> bucket : oldBuckets) {
            for (Node node : bucket) {
                buckets[bucketIndex(node.key)].add(node);
            }
        }
    }

    @Override
    public V get(K key) {
        Node node = findNode(buckets[bucketIndex(key)], key);
        return node == null ? null : node.value;
    }

    @Override
    public boolean containsKey(K key) {
        return findNode(buckets[bucketIndex(key)], key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                keys.add(node.key);
            }
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        Collection<Node> bucket = buckets[bucketIndex(key)];
        Node target = findNode(bucket, key);
        if (target == null) {
            return null;
        }
        bucket.remove(target);
        size--;
        return target.value;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
