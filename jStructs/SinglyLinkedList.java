package jStructs;
import java.lang.annotation.*;
import java.util.Optional;
import java.lang.reflect.*;

/**
 * @author <a href="https://github.com/carlosreyna0">Carlos Reyna</a>
 */
public class SinglyLinkedList<T, T1>
{
    private SinglyNode<T> current;
    private T1 eventClassInstance;
    private Class<T1> eventClass;

    public SinglyLinkedList(){}

    /**
     * Methods with the {@code NullTraverseEvent} annotation must only have 1 argument, and the argument should be of type
     * {@code SinglyLinkedList<T, T1>}. If these requirements are not met, the reflection will fail and an error
     * will be produced.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface NullTraverseEvent{}

    /**
     * Methods with the {@code TraverseEvent} annotation must only have 1 argument, and the argument should be of type
     * {@code SinglyLinkedList<T, T1>}. If these requirements are not met, the reflection will fail and an error
     * will be produced.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface TraverseEvent{}

    /**
     * Methods with the {@code SetEvent} annotation must only have 1 argument, and the argument should be of type
     * {@code SinglyLinkedList<T, T1>}. If these requirements are not met, the reflection will fail and an error
     * will be produced.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface SetEvent{}

    public enum Event
    {
        TRAVERSE,
        NULL_TRAVERSE,
        SET
    }

    /**
     * @return A new {@code SinglyLinkedList} object from the specified {@code SinglyNode}.
     */
    public static <T, T1> SinglyLinkedList<T, T1> from(SinglyNode<T> head)
    {
        SinglyLinkedList<T, T1> list = new SinglyLinkedList<>();
        list.setCurrent(head);

        return list;
    }
    public static <T, T1> SinglyLinkedList<T, T1> from(SinglyNode<T> head, T1 eventClass)
    {
        SinglyLinkedList<T, T1> toReturn = new SinglyLinkedList<>();
        toReturn.setEventClass(eventClass);

        toReturn.setCurrent(head);
        return toReturn;
    }

    public void setEventClass(T1 eventClass)
    {
        this.eventClassInstance = eventClass;
        this.eventClass = (Class<T1>) this.eventClassInstance.getClass();
    }

    /**
     * Traverses the list by the specified amount of times. If {@code amount} is 1, then the list's current node will
     * be the next one after it. Keep in mind that you can only traverse forward (or 'next' in jStructs) with a singly linked list.
     * If you wish to traverse forward and backward, check out {@code jStructs.DoublyLinkedList}.
     * @param amount The amount to traverse
     */
    public void traverseNext(int amount)
    {
        SinglyNode<T> current = this.current;
        boolean nullTraversed = false;

        for(int i = 0; i < amount; i++)
        {
            current = current.getNext().orElse(null);
            this.setCurrent(current, false);

            if(current == null)
            {
                this.fire(Event.NULL_TRAVERSE);
                nullTraversed = true;
                break;
            }
        }

        if(!nullTraversed) this.fire(Event.TRAVERSE);
    }

    /**
     Traverses to the end of the list.
     */
    public void traverseToEnd()
    {
        this.traverseNext(this.length());
    }

    /**
     Sets the list's current node.
     @param current The node to set the current node as. {@code null} is a valid value.
     */
    public void setCurrent(SinglyNode<T> current)
    {
        this.setCurrent(current, true);
    }

    private void setCurrent(SinglyNode<T> current, boolean fire)
    {
        this.current = current;
        if(fire) this.fire(Event.SET);
    }

    /**
     * @return The amount of nodes in the list.
     */
    public int length()
    {
        SinglyNode<T> current = this.current;
        int length = 0;

        if(this.current != null) length = 1;
        while((current = current.getNext().orElse(null)) != null)
        {
            length ++;
        }

        return length;
    }

    public void fire(Event e)
    {
        if(this.eventClassInstance == null)
        {
            return;
        }

        Method[] methods = this.eventClass.getDeclaredMethods();

        switch(e)
        {
            case NULL_TRAVERSE:
                for(Method m : methods)
                {
                    if(m.getParameterCount() == 1 && m.getDeclaredAnnotation(NullTraverseEvent.class) != null)
                    {
                        try
                        {
                            m.invoke(this.eventClassInstance, this);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
            case SET:
                for(Method m : methods)
                {
                    if(m.getParameterCount() == 1 && m.getDeclaredAnnotation(SetEvent.class) != null)
                    {
                        try
                        {
                            m.invoke(this.eventClassInstance, this);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
            case TRAVERSE:
                for(Method m : methods)
                {
                    if(m.getParameterCount() == 1 && m.getDeclaredAnnotation(TraverseEvent.class) != null)
                    {
                        try
                        {
                            m.invoke(this.eventClassInstance, this);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    /**
     * @return An {@code Optional<SinglyNode<T>>} containing the current node.
     */
    public Optional<SinglyNode<T>> getCurrent()
    {
        return Optional.ofNullable(this.current);
    }

    public boolean equals(Object o)
    {
        return this.getCurrent().isPresent() && this.getCurrent().equals(o);
    }

    public String toString()
    {
        return this.getCurrent().isPresent() ? this.getCurrent().get().toString() : "{}";
    }

    public int hashCode()
    {
        return this.length();
    }

    public Object clone()
    {
        return SinglyLinkedList.from(this.getCurrent().orElse(null));
    }
}