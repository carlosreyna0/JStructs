package jStructs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Optional;
import java.lang.reflect.*;

/**
 * @author <a href="https://github.com/carlosreyna0">Carlos Reyna</a>
 */
//todo : if an event is null do NOT call it
public class ArrList<T, T1> {
    private Object[] array = new Object[0];
    private Class<T1> eventClass;
    private T1 eventClassInstance;
    private T addedItem, removedItem, setItem;
    private int addedIndex, removedIndex, setIndex;

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SetEvent{}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface  AddedEvent{}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RemovedEvent{}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface InvalidIndexEvent{}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Filter{}

    public enum Event {
        SET,
        ADDED,
        REMOVED,
        INVALID_INDEX
    }

    public static <T, T1> ArrList<T, T1> from(T[] array)
    {
        ArrList<T, T1> list = new ArrList<>();

        for(int i = 0; i < array.length; i++)
        {
            list.add(array[i]);
        }

        return list;
    }

    public static <T, T1> ArrList<T, T1> from(T1 eventInstance)
    {
        ArrList<T, T1> list = new ArrList<>();
        list.setEventClass(eventInstance);

        return list;
    }

    public static <T, T1> ArrList<T, T1> from(T[] array, T1 eventInstance)
    {
        ArrList<T, T1> list = ArrList.from(array);
        list.setEventClass(eventInstance);

        return list;
    }

    public void add(T item)
    {
        Object[] newArray = new Object[this.array.length + 1];

        for(int i = 0; i < this.array.length; i++)
        {
            newArray[i] = this.array[i];
        }

        newArray[this.array.length] = item;

        this.addedItem = item;
        this.addedIndex = this.array.length;
        this.array = newArray;

        this.fire(Event.ADDED);
    }

    public void add(int index, T item)
    {
        Object[] newArray = new Object[this.array.length + 1];

        for(int i = 0; i < this.array.length; i++)
        {
            if(i < index)
            {
                newArray[i] = this.array[i];
            }
            else if(i == index)
            {
                newArray[i] = item;
            }
            else if(i > index)
            {
                newArray[i] = this.array[i - 1];
            }
        }

        newArray[this.array.length] = this.array[this.array.length - 1];

        this.addedItem = item;
        this.addedIndex = index;
        this.array = newArray;

        this.fire(Event.ADDED);
    }

    public void remove()
    {
        if(this.array.length == 1)
        {
            this.clear();
            return;
        }
        if(this.array.length == 0)
        {
            this.fire(Event.INVALID_INDEX);
            return;
        }

        Object[] newArray = new Object[this.array.length - 1];

        for(int i = 0; i < this.array.length - 1; i++)
        {
            newArray[i] = this.array[i];
        }

        this.removedItem = (T) this.array[newArray.length];
        this.removedIndex = newArray.length;
        this.array = newArray;

        this.fire(Event.REMOVED);

    }

    public void removeAll(T item)
    {
        int[] indexes = this.indexesOf(item);
        int offset = 0;
        for(int i : indexes)
        {
            this.remove(i - offset);
            offset++;
        }
    }

    public void remove(int index)
    {
        Object[] newArr = new Object[this.array.length - 1];

        for(int i = 0; i < this.array.length - 1; i++)
        {
            if(i < index)
            {
                newArr[i] = this.array[i];
            }
            else if(i >= index)
            {
                newArr[i] = this.array[i + 1];
            }
        }

        this.removedIndex = index;
        this.removedItem = (T) this.array[index];
        this.array = newArr;

        this.fire(Event.REMOVED);

    }

    public void set(int index, T item)
    {
        this.fire(Event.SET);

        this.array[index] = item;
        this.setIndex = index;
        this.setItem = item;
    }

    public Optional<T> get(int index)
    {
        return Optional.ofNullable((T)this.array[index]);
    }

    public boolean contains(T item)
    {
        for(Object o : this.array)
        {
            if(o.equals(item)) return true;
        }
        return false;
    }

    public int occurrencesOf(T item)
    {
        int occurences = 0;

        for(Object o : this.array)
        {
            if(o.equals(item)) occurences++;
        }

        return occurences;
    }

    public int[] indexesOf(T item)
    {
        int occurences = this.occurrencesOf(item), offset = 0;
        int[] indexes = new int[occurences];

        for(int i = 0; i < this.array.length; i++)
        {
            if(this.array[i].equals(item))
            {
                indexes[offset] = i;
                offset++;
            }
        }

        return indexes;
    }

    public void fire(Event e)
    {
        Method[] methods = this.eventClass.getDeclaredMethods();

        for(Method m : methods)
        {
            switch(e)
            {
                case INVALID_INDEX:
                    if(m.getParameterCount() != 1 || m.getDeclaredAnnotation(InvalidIndexEvent.class) == null)
                    {
                        continue;
                    }
                    try
                    {
                        m.invoke(this.eventClassInstance, this);
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    break;
                case REMOVED:
                    if(m.getParameterCount() != 3 || m.getDeclaredAnnotation(RemovedEvent.class) == null)
                    {
                        continue;
                    }
                    try
                    {
                        m.invoke(this.eventClassInstance, this, this.removedItem, this.removedIndex);
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    break;
                case ADDED:
                    if(m.getParameterCount() != 3 || m.getDeclaredAnnotation(AddedEvent.class) == null)
                    {
                        continue;
                    }
                    try
                    {
                        m.invoke(this.eventClassInstance, this, this.addedItem, this.addedIndex);
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    break;
                case SET:
                    if(m.getParameterCount() != 3 || m.getDeclaredAnnotation(SetEvent.class) == null)
                    {
                        continue;
                    }
                    try
                    {
                        m.invoke(this.eventClassInstance, this, this.setItem, this.setIndex);
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void clear()
    {
        this.array = new Object[0];
    }

    public void setEventClass(T1 eventClassInstance)
    {
        this.eventClass = (Class<T1>) eventClassInstance.getClass();
        this.eventClassInstance = eventClassInstance;
    }

    public int length()
    {
        return this.array.length;
    }

    public T[] toArray()
    {
        return (T[]) this.array;
    }

    @Override
    public String toString()
    {
        return Arrays.toString(this.array);
    }

    @Override
    public int hashCode()
    {
        return this.length();
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof ArrList)) return false;
        return Arrays.equals(((ArrList<?, ?>) o).array, this.array) && (((ArrList<?, ?>) o).eventClassInstance == this.eventClassInstance);
    }

    @Override
    public Object clone()
    {
        return ArrList.from(this.array, this.eventClassInstance);
    }


}