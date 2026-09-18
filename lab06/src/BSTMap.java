import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** A BST-based implementation of the Map61B interface.
 *  @param <K> the type of keys; must be Comparable so that the BST
 *             ordering is well defined.
 *  @param <V> the type of values.
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    /** Represents a node of the binary search tree. */
    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private BSTNode root;
    private int size;

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    /** Recursive helper: inserts key/value into the subtree rooted at node,
     *  replacing the value if the key is already present. Returns the
     *  (possibly new) root of the subtree. */
    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            size++;
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public V get(K key) {
        BSTNode node = get(root, key);
        return node == null ? null : node.value;
    }

    /** Recursive helper: returns the node with the given key in the subtree
     *  rooted at node, or null if no such node exists. */
    private BSTNode get(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        }
        return node;
    }

    @Override
    public boolean containsKey(K key) {
        return get(root, key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new TreeSet<>();
        collectKeys(root, keys);
        return keys;
    }

    /** Adds every key in the subtree rooted at node to keys. */
    private void collectKeys(BSTNode node, Set<K> keys) {
        if (node == null) {
            return;
        }
        collectKeys(node.left, keys);
        keys.add(node.key);
        collectKeys(node.right, keys);
    }

    @Override
    public V remove(K key) {
        V[] removed = (V[]) new Object[1];
        root = remove(root, key, removed);
        return removed[0];
    }

    /** Recursive helper: removes key from the subtree rooted at node.
     *  Stores the value that was removed (or null) in removed[0].
     *  Returns the (possibly new) root of the subtree. */
    private BSTNode remove(BSTNode node, K key, V[] removed) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key, removed);
        } else if (cmp > 0) {
            node.right = remove(node.right, key, removed);
        } else {
            removed[0] = node.value;
            size--;
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            // Two children: replace with the smallest key in the right subtree.
            BSTNode successor = node.right;
            BSTNode successorParent = node;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }
            node.key = successor.key;
            node.value = successor.value;
            if (successorParent == node) {
                node.right = successor.right;
            } else {
                successorParent.left = successor.right;
            }
        }
        return node;
    }

    @Override
    public Iterator<K> iterator() {
        List<K> keys = new ArrayList<>();
        collectKeysInOrder(root, keys);
        return keys.iterator();
    }

    /** Collects the keys of the subtree rooted at node into keys in
     *  ascending order. */
    private void collectKeysInOrder(BSTNode node, List<K> keys) {
        if (node == null) {
            return;
        }
        collectKeysInOrder(node.left, keys);
        keys.add(node.key);
        collectKeysInOrder(node.right, keys);
    }

    /** Prints the keys of this BSTMap in ascending order. */
    public void printInOrder() {
        for (K key : this) {
            System.out.print(key + " ");
        }
    }
}
