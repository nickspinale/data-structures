import java.util.*;
import java.util.function.*;
import java.util.Optional.*;

public class SortedLinkedList implements SortedList<ZooAnimal> {

    private Cons<ZooAnimal> innards;

    SortedLinkedList() {
        innards = new Cons<ZooAnimal>();
    }

    /**
     * Adds item to the list in sorted order.
     */
    public void add(ZooAnimal item) {
        innards = innards.insert(item);
    }

    /**
     * Remove targetItem from the list, shifting everything after it up
     * one position.
     * @return true if the item was in the list, false otherwise
     */
    public boolean remove(ZooAnimal targetItem) {
        Pair<Boolean,Cons<ZooAnimal>> pair = innards.unsert(targetItem);
        innards = pair.tail;
        return pair.head;
    }

    /**
     * Returns the position of targetItem in the list.
     * @return the position of the item, or -1 if targetItem is not i the list
     */
    public int getPosition(ZooAnimal targetItem) {
        return innards.lookup(0, targetItem).orElse(-1);
    }

    /** Returns the item at a given index.
     * @return the item, or throw an IndepairOutOfBoundsException if the index is out of bounds.
     */
    public ZooAnimal get(int position) {
        return innards.index(position).orElse((new ZooAnimal[0])[1]);
    }

    /** Returns true if the list contains the target item. */
    public boolean contains(ZooAnimal targetItem) {
        return innards.elem(targetItem);
    }

    /** Returns the length of the list: the number of items stored in it. */
    public int size() {
        return innards.length();
    }

    /** Returns true if the list has no items stored in it. */
    public boolean isEmpty() {
        return !innards.get.isPresent();
    }

    /** Returns an array version of the list.  Note that, for technical reasons,
     * the type of the items contained in the list can't be communicated
     * properly to the caller, so an array of Objects gets returned.
     * @return an array of length length(), with the same items in it as are
     *         stored in the list, in the same order.
     */
    public Object[] toArray() {
        ZooAnimal[] arr = new ZooAnimal[innards.length()];
        innards.confine(arr, 0);
        return arr;
    }

    /** Returns an iterator that begins just before index 0 in this list. */
    public Iterator<ZooAnimal> iterator() throws NoSuchElementException {
        return new Iterator<ZooAnimal>() {

            Cons<ZooAnimal> current;

            public boolean hasNext() {
                return current.get.map(((Pair<ZooAnimal,Cons<ZooAnimal>>) pair) -> pair.tail.isPresent().orElse(false).booleanValue();
            }

            public ZooAnimal next() {
                Pair<ZooAnimal,Cons<ZooAnimal>> tmp = current.get.get();
                current = tmp.tail;
                return tmp.head;
            }
        };
    }

    /** Removes all items from the list. */
    public void clear() {
        innards = new Cons<ZooAnimal>();
    }
}

// If only type synonyms epairisted...
class Cons<T extends Comparable<T>> {

    final Optional<Pair<T,Cons<T>>> get;

    Cons() {
        get = Optional.empty();
    }

    Cons(T head) {
        get = Optional.of(new Pair<T,Cons<T>>(head, new Cons<T>()));
    }

    Cons(T head, Cons<T> tail) {
        get = Optional.of(new Pair<T,Cons<T>>(head, tail));
    }

    Cons<T> insert(T e) {
        System.out.println("HHHH");
        return get.map(pair ->
            pair.head.compareTo(e) < 1
          ? new Cons<T>(pair.head, pair.tail.insert(e))
          : new Cons<T>(e, this)
        ).orElse(new Cons<T>(e));
    }

    Pair<Boolean,Cons<T>> unsert(T e) {
        return get.map(pair -> {
            if(pair.head == e) {
                return new Pair<Boolean,Cons<T>>(true, pair.tail);
            } else {
                final Pair<Boolean,Cons<T>> tmp = pair.tail.unsert(e);
                return new Pair<Boolean,Cons<T>>(tmp.head, new Cons<T>(pair.head, tmp.tail));
            }
        }).orElse(new Pair<Boolean,Cons<T>>(false, this));
    }

    Optional<Integer> lookup(Integer i, T val) {
        return get.map(pair ->
            pair.head == val
          ? Optional.of(i)
          : ( pair.head.compareTo(val) > 0
            ? pair.tail.lookup(i + 1, val)
            : Optional.<Integer>empty()
            )
        ).orElse(Optional.empty());
    }

    Optional<T> index(Integer i) {
        return get.map(pair ->
            i == 0 ? Optional.of(pair.head) : pair.tail.index(i - 1)
        ).orElse(Optional.empty());
    }

    boolean elem(T val) {
        return get.map(pair ->
            val == pair.head ? true : pair.tail.elem(val)
        ).orElse(false);
    }

    int length() {
        return get.map(pair ->
            1 + pair.tail.length()
        ).orElse(0);
    }

    void confine(T[] arr, int i) {
        get.ifPresent(pair -> {
            arr[i] = pair.head;
            pair.tail.confine(arr, i + 1);
    });}
}

// A pair...
class Pair<F,S> {

    final F head;
    final S tail;

    Pair(F head, S tail) {
        this.head = head;
        this.tail = tail;
    }
}
