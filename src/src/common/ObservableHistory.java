package common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ObservableHistory<E, I> extends common.Observable<E> {

    protected List<I> historyList = new ArrayList<>();

    public void subscribe(E value)
    {
        super.subscribe(value);
        try {
            for (I item : historyList) {
                if (value instanceof common.CallbackAble)
                    ((common.CallbackAble<I>) value).executeCallback(item);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addHistoryItem(I value)
    {
        historyList.add(value);
    }

    public List<I> getHistoryList(){
        return historyList;
    }
}
