import jh61b.utils.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

/** Performs some basic linked list tests. */
public class LinkedListDeque61BTest {

    @Test
    /** In this test, we have three different assert statements that verify that addFirst works correctly. */
    public void addFirstTestBasic() {
        Deque61B<String> lld1 = new LinkedListDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]
        assertThat(lld1.toList()).containsExactly("back").inOrder();

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]
        assertThat(lld1.toList()).containsExactly("middle", "back").inOrder();

        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]
        assertThat(lld1.toList()).containsExactly("front", "middle", "back").inOrder();
    }

    @Test
    /** In this test, we use only one assertThat statement. IMO this test is just as good as addFirstTestBasic.
     *  In other words, the tedious work of adding the extra assertThat statements isn't worth it. */
    public void addLastTestBasic() {
        Deque61B<String> lld1 = new LinkedListDeque61B<>();

        lld1.addLast("front"); // after this call we expect: ["front"]
        lld1.addLast("middle"); // after this call we expect: ["front", "middle"]
        lld1.addLast("back"); // after this call we expect: ["front", "middle", "back"]
        assertThat(lld1.toList()).containsExactly("front", "middle", "back").inOrder();
    }

    @Test
    /** This test performs interspersed addFirst and addLast calls. */
    public void addFirstAndAddLastTest() {
        Deque61B<Integer> lld1 = new LinkedListDeque61B<>();

        /* I've decided to add in comments the state after each call for the convenience of the
           person reading this test. Some programmers might consider this excessively verbose. */
        lld1.addLast(0);   // [0]
        lld1.addLast(1);   // [0, 1]
        lld1.addFirst(-1); // [-1, 0, 1]
        lld1.addLast(2);   // [-1, 0, 1, 2]
        lld1.addFirst(-2); // [-2, -1, 0, 1, 2]

        assertThat(lld1.toList()).containsExactly(-2, -1, 0, 1, 2).inOrder();
    }

    @Test
    /** Checks that isEmpty and size behave correctly through adds and removes. */
    public void sizeIsEmptyTest() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();
        assertThat(deque.isEmpty()).isTrue();
        assertThat(deque.size()).isEqualTo(0);

        deque.addFirst(5);
        deque.addLast(10);
        assertThat(deque.isEmpty()).isFalse();
        assertThat(deque.size()).isEqualTo(2);

        deque.removeFirst();
        deque.removeLast();
        assertThat(deque.isEmpty()).isTrue();
        assertThat(deque.size()).isEqualTo(0);
    }

    @Test
    /** Checks removeFirst and removeLast, including on an empty deque. */
    public void removeTest() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();
        assertThat(deque.removeFirst()).isNull();
        assertThat(deque.removeLast()).isNull();

        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        assertThat(deque.removeFirst()).isEqualTo(1);
        assertThat(deque.removeLast()).isEqualTo(3);
        assertThat(deque.toList()).containsExactly(2).inOrder();
        assertThat(deque.removeFirst()).isEqualTo(2);
        assertThat(deque.isEmpty()).isTrue();
    }

    @Test
    /** Checks the iterative get method, including out-of-bounds indices. */
    public void getTest() {
        Deque61B<String> deque = new LinkedListDeque61B<>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("c");

        assertThat(deque.get(0)).isEqualTo("a");
        assertThat(deque.get(1)).isEqualTo("b");
        assertThat(deque.get(2)).isEqualTo("c");
        assertThat(deque.get(-1)).isNull();
        assertThat(deque.get(3)).isNull();
        assertThat(deque.toList()).containsExactly("a", "b", "c").inOrder();
    }

    @Test
    /** Checks the recursive get method, including out-of-bounds indices. */
    public void getRecursiveTest() {
        Deque61B<String> deque = new LinkedListDeque61B<>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("c");

        assertThat(deque.getRecursive(0)).isEqualTo("a");
        assertThat(deque.getRecursive(1)).isEqualTo("b");
        assertThat(deque.getRecursive(2)).isEqualTo("c");
        assertThat(deque.getRecursive(-1)).isNull();
        assertThat(deque.getRecursive(3)).isNull();
        assertThat(deque.toList()).containsExactly("a", "b", "c").inOrder();
    }

    @Test
    /** Checks that get and getRecursive agree after a mix of operations. */
    public void getAgreementTest() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        deque.removeFirst();
        deque.removeLast();
        for (int i = 0; i < deque.size(); i++) {
            assertThat(deque.get(i)).isEqualTo(deque.getRecursive(i));
        }
    }
}
