import java.util.Optional.*

public class SortedLinkedList implements SortedList<ZooAnimal> {

    List<ZooAnimal> innards;

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
    public Iterator<ZooAnimal> iterator() {
        return new Iterator<ZooAnimal> {

            List<ZooAnimal> current;

            public boolean hasNext() {
                return current.isPresent();
            }

            public ZooAnimal next() {
                try {
                    Pair<T,List<T>> tmp = current.get();
                    current = tmp.tail;
                    return tmp.head;
                } catch(Exception e) {
                    throw new NoSuchElementException();
                }
            }
        }
    }

    /** Removes all items from the list. */
    public void clear() {
        innards = new List<ZooAnimal>;
    }
}

// If only type synonyms existed...
private class List<T> {

    final Optional<Pair<T,List<T>>> get;

    List(T head, List<T> tail) {
        get = Optional.of(new Pair(head, tail));
    }

    U casify(U baseCase, Function<Pair<T,List<T>>,U> f) {
        get.map(f).orElse(baseCase);
    }

    List<T> insert(T e) {
        return casifiy(new List(e), x ->
            x.head.compare(e) < 1 ? x.attach(y -> y.insert(e)) : new List(e, list)
    );}

    Pair<Boolean,List<T>> unsert(T e) {
        return casify(new Pair(false, this), x ->
            x.head == e ?
            new Pair(true, x.tail) :
            x.tail.unsert(e).attach(y -> new List<T>(x.head, y));
    );}

    Optional<Integer> lookup(Integer i, T val) {
        return casify(Optional.empty(), x ->
            x.head == val ? Optonal.of(i) : x.tail.lookup(i + 1, val)
    );}

    Optional<T> index(Integer i) {
        return casify(Optional.empty(), x ->
            i == 0 ? Optonal.of(x.head) : x.tail.index(i - 1, val)
    );}

    boolean elem(T val) {
        return casify(false, x ->
            val == x.head ? true : x.tail.elem(val)
    );}

    int length() {
        return casify(0, x ->
            1 + length(x.tail)
    );}

    void confine(T[] arr, int i) {
        get.ifPresent(x -> {
            arr[i] = x.head;
            x.tail.confine(arr, i + 1)
    });}
}

// A pair...
private class Pair<F,S> {

    final F head;
    final S tail;

    Pair(F head, S tail) {
        this.head = head;
        this.tail = tail;
    }

    Pair<F,S> attach(BinaryOperator<S> f) {
        return new Pair(head, f.apply(tail));
    }
}
