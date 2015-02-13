import java.util.*;
import java.util.function.*;
import java.util.Optional.*;

public class SortedLinkedList implements SortedList<ZooAnimal> {

    Cons<ZooAnimal> innards;

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
    public Iterator<ZooAnimal> iterator() {
        return new Iterator<ZooAnimal>() {

            Cons<ZooAnimal> current;

            public boolean hasNext() {
                return current.get.isPresent();
            }

            public ZooAnimal next() {
                try {
                    Pair<ZooAnimal,Cons<ZooAnimal>> tmp = current.get.get();
                    current = tmp.tail;
                    return tmp.head;
                } catch(Exception e) {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    /** Removes all items from the list. */
    public void clear() {
        innards = new Cons<ZooAnimal>();
    }
}

// If only type synonyms epairisted...
class Cons<? extends Comparable<T>> {

    final Optional<Pair<T,Cons<T>>> get;

    Cons() {
        get = Optional.empty();
    }

    Cons(T head) {
        get = Optional.of(new Pair(head, Optional.empty()));
    }

    Cons(T head, Cons<T> tail) {
        get = Optional.of(new Pair(head, tail));
    }

    <U> U casify(U baseCase, Function<Pair<T,Cons<T>>,U> f) {
        get.map(f).orElse(baseCase);
    }

    Cons<T> insert(T e) {
        return casify(new Cons(e), pair ->
            pair.head.compare(e) < 1 ? pair.attach(x -> x.insert(e)) : new Cons(e, this)
    );}

    Pair<Boolean,Cons<T>> unsert(T e) {
        return casify(new Pair(false, this), pair ->
            pair.head == e ?
            new Pair(true, pair.tail) :
            pair.tail.unsert(e).attach(x -> new Cons<T>(pair.head, x))
    );}

    Optional<Integer> lookup(Integer i, T val) {
        return casify(Optional.empty(), pair ->
            pair.head == val ?
            Optional.of(i) :
            (pair.head.compare(val) > 0 ? pair.tail.lookup(i + 1, val) : Optional.empty())
    );}

    Optional<T> index(Integer i) {
        return casify(Optional.empty(), pair ->
            i == 0 ? Optional.of(pair.head) : pair.tail.index(i - 1)
    );}

    boolean elem(T val) {
        return casify(false, pair ->
            val == pair.head ? true : pair.tail.elem(val)
    );}

    int length() {
        return casify(0, pair ->
            1 + pair.tail.length()
    );}

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

    <T> Pair<F,T> attach(Function<S,T> f) {
        return new Pair(head, f.apply(tail));
    }
}
