package common;

import java.util.HashSet;
import java.util.Set;

public class ObjectProperty<E> {

    private E containedValue;

    private Set<PropertyChangedCallback> callbackSet = new HashSet<>();

    public ObjectProperty(E containedValue)
    {
        this.containedValue = containedValue;
    }

    public E getValue()
    {
        return containedValue;
    }

    public void setValue(E value)
    {
        notifyChanges(containedValue, value);
        this.containedValue = value;
    }

    public void addListener(PropertyChangedCallback<E> propertyChangedCallback)
    {
        callbackSet.add(propertyChangedCallback);
        if(containedValue != null)
            notifyChanges(null, containedValue);
    }

    private void notifyChanges(E oldValue, E value)
    {
        for (PropertyChangedCallback changedCallback : callbackSet)
            changedCallback.onChanged(oldValue, value);
    }

    public interface PropertyChangedCallback<E>
    {
        void onChanged(E oldValue, E newValue);
    }
}
