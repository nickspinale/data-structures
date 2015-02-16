import java.util.*;
import java.util.function.*;
import java.util.Optional.*;

public class SortedLinkedList implements SortedList<ZooAnimal> {

    // The sortedlinkedlist class is merely a SortedList-shaped wrapper
    // for a Cons object. This is that Cons.
    // I recommend looking at the Cons class before this one.
    private Cons<ZooAnimal> innards;

    /**
     * Constructor does nothing but initialize innards as an empty list.
     */
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
     * Note that we don't use .ifPresent() because a throw doesn't satisfy the return requirement.
     */
    public ZooAnimal get(int position) {
        Optional<ZooAnimal> tmp = innards.index(position);
        if(tmp.isPresent()) {
            return tmp.get();
        } else {
            throw new IndexOutOfBoundsException();
        }
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

            Cons<ZooAnimal> current = innards;

            // See below for an explanation of optionals
            public boolean hasNext() {
                return current.get.isPresent();
            }

            public ZooAnimal next() {
                Pair<ZooAnimal,Cons<ZooAnimal>> tmp = current.get.get();
                current = tmp.tail;
                return tmp.head;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /** Removes all items from the list. */
    public void clear() {
        innards = new Cons<ZooAnimal>();
    }
}

/**
 * This is basically a nice, purely functional linked list.
 * This sort of list is defined as EITHER a pair consisting of a head (a value)
 * and a tail (another list, of the sort being defined right now), OR nothing
 * (the empty list). This is implemented using an optional. An optional
 * parameterized over a type T is either a T or nothing (it is basically a wrapped
 * reference that makes null values your friend). Example uses:
 *   Optional.of(e) => a wrapper for e
 *   Optional.empty() => a wrapped null value
 *   opt.map(f) => if opt contains a value, returns an optional wrapping f(that value),
                   otherwise does nothing (returns a wrapped null value, which is
                   already is).
 * In pseudocode, Optional T = Just T | Nothing
 *   Optional.of(e) => Just e
 *   Optional.empty() => Nothing
 *   opt.map(f) => case opt of
 *                   Just e -> Just f(e)
 *                   Nothing -> Nothing
 */
class Cons<T extends Comparable<T>> {

    // The pair that this class wraps.
    // NOTE this is final. Methods in this class return new cons' rather than
    // altering themselves
    final Optional<Pair<T,Cons<T>>> get;

    // Empty list
    Cons() {
        get = Optional.empty();
    }

    // Singleton
    Cons(T head) {
        get = Optional.of(new Pair<T,Cons<T>>(head, new Cons<T>()));
    }

    // Normal cons
    Cons(T head, Cons<T> tail) {
        get = Optional.of(new Pair<T,Cons<T>>(head, tail));
    }

    /* Most of the following methods have this structure:
     * f(x) = get.map(g).orElse(default), where x and default are of type U
     * for some U, and g is of type Pair<T,Cons<T>> -> U. So basically,
     * most of these methods have one behavior for the empty list, an
     * another non-empty lists.
     * If we are to use the same pseudocode as before, this looks like:
     * f(x) = case get of
     *          Just pair -> g(pair)
     *          Nothing -> default
     * Also, note that, as noted before, all of these methodsm return new objects.
     */

    Cons<T> insert(T e) {
        return get.map(pair ->
            // If head is smaller than e, insert e here.
            pair.head.compareTo(e) > 0
          ? new Cons<T>(e, this)
          : new Cons<T>(pair.head, pair.tail.insert(e))
        ).orElse(new Cons<T>(e));
    }

    // This returns a pair that is (whether anything was removes, the resulting list)
    Pair<Boolean,Cons<T>> unsert(T e) {
        return get.map(pair -> {
            // The ternary operator does not work here because we need to use
            // a temporary variable to deal with the pair
            if(pair.head == e) {
                return new Pair<Boolean,Cons<T>>(true, pair.tail);
            } else {
                final Pair<Boolean,Cons<T>> tmp = pair.tail.unsert(e);
                // Here, we return the tmp (the call on tail), but we cons head with the resulting list.
                return new Pair<Boolean,Cons<T>>(tmp.head, new Cons<T>(pair.head, tmp.tail));
            }
        }).orElse(new Pair<Boolean,Cons<T>>(false, this));
    }

    // Cons' getPosition() method.
    Optional<Integer> lookup(Integer i, T val) {
        return get.map(pair ->
            pair.head == val
          ? Optional.of(i)
          // We only need to continue if head is smaller than val
          : ( pair.head.compareTo(val) > 0
            ? Optional.<Integer>empty()
            : pair.tail.lookup(i + 1, val)
            )
        ).orElse(Optional.empty());
    }

    // Cons' get() method.
    Optional<T> index(Integer i) {
        return get.map(pair ->
            i == 0 ? Optional.of(pair.head) : pair.tail.index(i - 1)
        ).orElse(Optional.empty());
    }

    // Cons' contains() method.
    boolean elem(T val) {
        return get.map(pair ->
            val == pair.head ? true : pair.tail.elem(val)
        ).orElse(false);
    }

    // Cons' size() method.
    int length() {
        return get.map(pair ->
            1 + pair.tail.length()
        ).orElse(0);
    }

    // Cons' toArray() method, where arr is the array to add to,
    // and i is the next index to be filled (within the array)
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
