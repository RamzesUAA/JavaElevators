package common;

import java.util.HashSet;
import java.util.Set;

public abstract class Observable<E> {

    protected Set<E> callbackSet = new HashSet<>();

    public void subscribe(E value)
    {
        callbackSet.add(value);
    }

    public void unsubscribe(E value)
    {
        callbackSet.remove(value);
    }
}
