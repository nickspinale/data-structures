import java.util.Optional.*

public class SortedLinkedList implements SortedList<ZooAnimal> {
    /**
     * Adds item to the list in sorted order.
     */
    public void add(T item) {
        innards = insert.apply(item).apply(innards);
    }

    /**
     * Remove targetItem from the list, shifting everything after it up
     * one position.
     * @return true if the item was in the list, false otherwise
     */
    public boolean remove(T targetItem) {
        Pair<Boolean,ZooAnimal> pair = unsert.apply(targetItem, innards);
        innards = pair.second;
        return pair.first;
    }

    /**
     * Returns the position of targetItem in the list.
     * @return the position of the item, or -1 if targetItem is not i the list
     */
    public int getPosition(T targetItem) {
        return lookup(0, targetItem, innards).orElse(-1);
    }

    /** Returns the item at a given index.
     * @return the item, or throw an IndexOutOfBoundsException if the index is out of bounds.
     */
    public T get(int position) {
        return index(position, innards).orElse(throw new IndexOutOfBoundsException());
    }

    /** Returns true if the list contains the target item. */
    public boolean contains(T targetItem) {
        return elem(targetItem, innards);
    }

    /** Returns the length of the list: the number of items stored in it. */
    public int size() {
        return length(innards);
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
        ZooAnimal[] arr = new ZooAnimal[innards.size()];
        confine(arr, 0, innards);
        return arr;
    }

    /** Returns an iterator that begins just before index 0 in this list. */
    public Iterator<T> iterator();

    /** Removes all items from the list. */
    public void clear() {
        innards = new List<ZooAnimal>;
    }

    List<ZooAnimal> innards;

    private UnaryOperator<List<T>> insert(T e) {
        return list -> list.get.map(x ->
            x.head.compare(e) < 1
            ? attach(insert(e), x)
            : new List(e, list)
        ).orElse(new List(e));
    }

    private Pair<Boolean,List<T>> unsert(T e, List<T> list) {
        return list.get.map(x ->
            x.head == e
            ? new Pair(true, x.tail)
            : .attach(cons(x.head), unsert(e, x.tail))
        ).orElse(new Pair(false, list));
    }

    private Optional<Integer> lookup(Integer i, T val, List<T> list) {
        return list.get.map(x ->
            x.head == val
            ? Optonal.of(i)
            : lookup(i + 1, val, x.tail)
        ).orElse(Optional.empty());
    }

    private Optional<T> index(Integer i, List<T> list) {
        return list.get.map(x ->
            i == 0
            ? Optonal.of(x.head)
            : index(i - 1, val, x.tail)
        ).orElse(Optional.empty());

    private boolean elem(T val, List<T> list) {
        return list.get.map(x ->
            val == x.head ? true : elem(val, x.tail)
        ).orElse(false);
    }

    private int length(List<T> list) {
        return list.get.map(x ->
            1 + length(x.tail)
        ).orElse(0);

    private void confine(T[] arr, int i, List<T> list) {
        list.ifPresent(x -> {
            arr[i] = x.head;
            confine(arr, i + 1, x.tail)
        });
    }

    // private U listCase(List<T> list, U emptyCase, Function<Pair<T,List<T>>> operation) {
    //     return list.get.map(operation).orElse(emptyCase);
    // }

    // YAY JAVA! CURRYING
    // NOTE: Java's type signatures don't do so well with currying,
    // so I would use the arguments and result to discern type.
    private final UnaryOperator<List<T>> cons(T head) {
        return tail -> new List(head, tail);
    }

    private Pair<F,S> attach(BinaryOperator<S> f, Pair<F,S> pair) {
        return new Pair(pair.first, f.apply(pair.second));
    }
}

// If only type synonyms existed...
private class List<T> {

    final Optional<Pair<T,List<T>>> get;

    List() {
        get = Optional.empty;
    }

    List(T head) {
        get = Optional.of(new Pair(head, new List()));
    }

    List(T head, List<T> tail) {
        get = Optional.of(new Pair(head, tail));
    }
}

// A pair...
private class Pair<F,S> {

    final F first;
    final S second;

    Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
