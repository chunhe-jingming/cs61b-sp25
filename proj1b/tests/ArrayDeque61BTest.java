import deque.ArrayDeque61B;

import jh61b.utils.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class ArrayDeque61BTest {

    @Test
    @DisplayName("ArrayDeque61B has no fields besides backing array and primitives")
    void noNonTrivialFields() {
        List<Field> badFields = Reflection.getFields(ArrayDeque61B.class)
                .filter(f -> !(f.getType().isPrimitive() || f.getType().equals(Object[].class) || f.isSynthetic()))
                .toList();

        assertWithMessage("Found fields that are not array or primitives").that(badFields).isEmpty();
    }

    @Test
    @DisplayName("addFirst and toList")
    void addFirstTest() {
        ArrayDeque61B<String> deque = new ArrayDeque61B<>();
        deque.addFirst("back");
        deque.addFirst("middle");
        deque.addFirst("front");
        assertThat(deque.toList()).containsExactly("front", "middle", "back").inOrder();
    }

    @Test
    @DisplayName("addLast and toList")
    void addLastTest() {
        ArrayDeque61B<String> deque = new ArrayDeque61B<>();
        deque.addLast("front");
        deque.addLast("middle");
        deque.addLast("back");
        assertThat(deque.toList()).containsExactly("front", "middle", "back").inOrder();
    }

    @Test
    @DisplayName("interspersed addFirst and addLast")
    void addFirstAndAddLastTest() {
        ArrayDeque61B<Integer> deque = new ArrayDeque61B<>();
        deque.addLast(0);
        deque.addLast(1);
        deque.addFirst(-1);
        deque.addLast(2);
        deque.addFirst(-2);
        assertThat(deque.toList()).containsExactly(-2, -1, 0, 1, 2).inOrder();
    }

    @Test
    @DisplayName("size and isEmpty")
    void sizeAndIsEmptyTest() {
        ArrayDeque61B<Integer> deque = new ArrayDeque61B<>();
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
    @DisplayName("removeFirst and removeLast")
    void removeTest() {
        ArrayDeque61B<Integer> deque = new ArrayDeque61B<>();
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
    @DisplayName("get with wrapping")
    void getTest() {
        ArrayDeque61B<String> deque = new ArrayDeque61B<>();
        deque.addFirst("a");
        deque.addFirst("b");
        deque.addFirst("c"); // [c, b, a]
        deque.addLast("d");  // [c, b, a, d]
        assertThat(deque.get(0)).isEqualTo("c");
        assertThat(deque.get(1)).isEqualTo("b");
        assertThat(deque.get(2)).isEqualTo("a");
        assertThat(deque.get(3)).isEqualTo("d");
        assertThat(deque.get(-1)).isNull();
        assertThat(deque.get(4)).isNull();
    }

    @Test
    @DisplayName("getRecursive agrees with get")
    void getRecursiveTest() {
        ArrayDeque61B<Integer> deque = new ArrayDeque61B<>();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        deque.removeFirst();
        deque.removeLast();
        for (int i = 0; i < deque.size(); i++) {
            assertThat(deque.getRecursive(i)).isEqualTo(deque.get(i));
        }
        assertThat(deque.getRecursive(-1)).isNull();
        assertThat(deque.getRecursive(deque.size())).isNull();
    }

    @Test
    @DisplayName("resize keeps order across many operations")
    void resizeTest() {
        ArrayDeque61B<Integer> deque = new ArrayDeque61B<>();
        // The initial capacity is 8; force several resizes.
        for (int i = 0; i < 1000; i++) {
            if (i % 2 == 0) {
                deque.addFirst(i);
            } else {
                deque.addLast(i);
            }
        }
        assertThat(deque.size()).isEqualTo(1000);
        for (int i = 0; i < 1000; i++) {
            deque.removeFirst();
        }
        assertThat(deque.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("add after removals wraps around correctly")
    void wrapAroundTest() {
        ArrayDeque61B<Integer> deque = new ArrayDeque61B<>();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        for (int i = 0; i < 50; i++) {
            assertThat(deque.removeFirst()).isEqualTo(i);
        }
        for (int i = 100; i < 200; i++) {
            deque.addLast(i);
        }
        for (int i = 50; i < 200; i++) {
            assertThat(deque.removeFirst()).isEqualTo(i);
        }
        assertThat(deque.isEmpty()).isTrue();
    }
}
