package data_struct;

import java.util.ArrayList;
import java.util.Iterator;

public class ArrayBox<T> implements Iterable<T> {
    private ArrayList<T> val;
    private int hash;

    public ArrayBox() {
        val = new ArrayList<>();
        hash = 672487;
    }

    public void add(T value) {
        this.val.add(value);
        hash *= val.size() * 672487 * value.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof ArrayBox && equals((ArrayBox) o);
    }

    public int size() {
        return val.size();
    }

    public T get(int index) {
        return val.get(index);
    }

    public boolean equals(ArrayBox ab) {
        if (ab.hashCode() != hashCode() || size() != ab.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!ab.get(i).equals(get(i)))
                return false;
        }
        return true;
    }

    public int hashCode() {
        return hash;
    }

    @Override
    public Iterator<T> iterator() {
        return val.iterator();
    }
}
