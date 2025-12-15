package jStructs;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import java.lang.reflect.*;

/**
 * @author <a href="https://github.com/carlosreyna0">Carlos Reyna</a>
 */
public class DoublyLinkedList<T, T1>
{
    private DoublyNode<T> current;
    private Class<T1> eventClass;
    private T1 eventClassInstance;

    /**
     * Methods with the {@code TraverseNextEvent} annotation must only have 1 argument, and the argument should be of type
     * {@code DoublyLinkedList<T, T1>}. If these requirements are not met, the reflection will fail and an error
     * will be produced.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TraverseNextEvent{}

    /**
     * Methods with the {@code TraversePrevEvent} annotation must only have 1 argument, and the argument should be of type
     * {@code DoublyLinkedList<T, T1>}. If these requirements are not met, the reflection will fail and an error
     * will be produced.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TraversePrevEvent{}

    /**
     * Methods with the {@code NullTraverseEvent} annotation must only have 1 argument, and the argument should be of type
     * {@code DoublyLinkedList<T, T1>}. If these requirements are not met, the reflection will fail and an error
     * will be produced.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NullTraverseEvent{}

    /**
     * Methods with the {@code SetEvent} annotation must only have 1 argument, and the argument should be of type
     * {@code DoublyLinkedList<T, T1>}. If these requirements are not met, the reflection will fail and an error
     * will be produced.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SetEvent{}

    public enum Event
    {
        TRAVERSE_NEXT,
        TRAVERSE_PREV,
        NULL_TRAVERSE,
        SET
    }

    /**
     * @return A new {@code DoublyLinkedList} from the specified {@code DoublyNode}.
     */
    public static <T,T1> DoublyLinkedList<T, T1> from(DoublyNode<T> node)
    {
        DoublyLinkedList<T,T1> list = new DoublyLinkedList<>();
        list.setCurrent(node);

        return list;
    }

    /**
     * @param node The {@code DoublyNode} to be used in the creation of a new {@code DoublyLinkedList}
     * @param eventClass An object, which will be reflected upon for events with annotations in {@code DoublyLinkedList}
     * @return A new {@code DoublyLinkedList} from the specified {@code DoublyNode} and the specified event object.
     */
    public static <T,T1> DoublyLinkedList<T, T1> from(DoublyNode<T> node, T1 eventClass)
    {
        DoublyLinkedList<T,T1> list = new DoublyLinkedList<>();
        list.setCurrent(node);
        list.setEventClass(eventClass);

        return list;
    }

    /**
     * Traverses the specified amount previously.
     * @param length The amount to traverse
     */
    public void traversePrev(int length)
    {
        DoublyNode<T> current = this.current;
        for(int i = 0; i < length; i++)
        {
            current = current.getPrev().orElse(null);
            if(current == null)
            {
                this.fire(Event.NULL_TRAVERSE);
                return;
            }
        }

        this.setCurrent(current);
        this.fire(Event.TRAVERSE_PREV);
    }

    /**
     * Traverses the specified amount next.
     * @param length The amount to traverse
     */
    public void traverseNext(int length)
    {
        DoublyNode<T> current = this.current;
        for(int i = 0; i < length; i++)
        {
            current = current.getNext().orElse(null);
            if(current == null)
            {
                this.fire(Event.NULL_TRAVERSE);
                this.setCurrent(new DoublyNode<>(null));
                return;
            }
        }

        this.setCurrent(current);
        this.fire(Event.TRAVERSE_NEXT);

    }

    /**
     * Sets the list's current node. If {@code null}, then the current gets set to a {@code new DoublyNode<>(null)}.
     * @param node The node to be the current node.
     */
    public void setCurrent(DoublyNode<T> node)
    {
        if(node == null)
        {
            this.setCurrent(new DoublyNode<>(null));
        }
        else
        {
            this.current = node;
        }

        this.fire(Event.SET);
    }

    /**
     * @return An {@code Optional} containing the current node.
     */
    public Optional<DoublyNode<T>> getCurrent()
    {
        return Optional.ofNullable(this.current);
    }

    /**
     * @return The amount of nodes in the list.
     */
    public int length()
    {
        if(this.current == null) return 0;
        DoublyNode<T> current = DoublyNode.startOf(this.current).orElse(null);
        int length = 1;

        while((current = current.getNext().orElse(null)) != null)
        {
            length++;
            if(current.getNext().isEmpty()) break;
        }

        return length;
    }

    public void setEventClass(T1 eventClass)
    {
        this.eventClassInstance = eventClass;
        this.eventClass = (Class<T1>) eventClass.getClass();
    }

    public void fire(Event e)
    {
        if(this.eventClassInstance == null) return;
        Method[] methods = this.eventClass.getDeclaredMethods();

        for(Method m : methods)
        {
            switch(e)
            {
                case NULL_TRAVERSE:
                    if(m.getDeclaredAnnotation(NullTraverseEvent.class) != null && m.getParameterCount() == 1)
                    {
                        try
                        {
                            m.invoke(this.eventClassInstance, this);
                        }catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case TRAVERSE_NEXT:
                    if(m.getDeclaredAnnotation(TraverseNextEvent.class) != null && m.getParameterCount() == 1)
                    {
                        try
                        {
                            m.invoke(this.eventClassInstance, this);
                        }catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case TRAVERSE_PREV:
                    if(m.getDeclaredAnnotation(TraversePrevEvent.class) != null && m.getParameterCount() == 1)
                    {
                        try
                        {
                            m.invoke(this.eventClassInstance, this);
                        }catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case SET:
                    if(m.getDeclaredAnnotation(SetEvent.class) != null && m.getParameterCount() == 1)
                    {
                        try
                        {
                            m.invoke(this.eventClassInstance, this);
                        }catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    break;
            }
        }

    }

    @Override
    public String toString()
    {
        if(this.current == null) return "{}";
        return this.current.toString();
    }

    @Override
    public int hashCode()
    {
        return this.length();
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof DoublyLinkedList)) return false;
        if(this.current == null) return false;

        return ((DoublyLinkedList<?, ?>) o).getCurrent().equals(this.current);
    }

    @Override
    public Object clone()
    {
        return DoublyLinkedList.from(this.getCurrent().orElse(null), this.eventClassInstance);
    }

}