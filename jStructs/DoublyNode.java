package jStructs;
import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * The node to be used in a {@code DoublyLinkedList}.
 * @author <a href="https://github.com/carlosreyna0">Carlos Reyna</a>
 */
public class DoublyNode<T>
{
    private DoublyNode<T> next, prev;
    private T value;

    public DoublyNode(T value)
    {
        this.set(value);
    }

    public DoublyNode(DoublyNode<T> prev, T value)
    {
        this.setPrev(prev);
        this.set(value);
    }

    public static <T> Optional<DoublyNode<T>> endOf(DoublyNode<T> node)
    {
        if(node.next == null)
        {
            return Optional.of(node);
        }

        DoublyNode<T> current = node;

        while((current = current.next) != null)
        {
            if(current.next == null) break;
        }

        return Optional.ofNullable(current);
    }

    public static <T> Optional<DoublyNode<T>> startOf(DoublyNode<T> node)
    {
        if(node.prev == null)
        {
            return Optional.ofNullable(node);
        }
        DoublyNode<T> current = node;

        while((current = current.prev) != null)
        {
            if(current.prev == null) break;
        }

        return Optional.ofNullable(current);
    }

    public void set(T value)
    {
        this.value = value;
    }

    public void setNext(DoublyNode<T> next)
    {
        next.prev = this;
        this.next = next;
    }
    public void setPrev(DoublyNode<T> prev)
    {
        prev.next = this;
        this.prev = prev;
    }

    public Optional<T> get()
    {
        return Optional.ofNullable(this.value);
    }

    public Optional<DoublyNode<T>> getPrev()
    {
        return Optional.ofNullable(this.prev);
    }

    public Optional<DoublyNode<T>> getNext()
    {
        return Optional.ofNullable(this.next);
    }

    @Override
    public String toString()
    {
        DoublyNode<T> start = DoublyNode.startOf(this).orElse(null);
        String stringed = "{" + start.value.toString() + "}";
        if(start.next != null) stringed += " <-> ";

        while((start = start.next) != null)
        {
            stringed += "{" + start.value + "}";
            if(start.next != null) stringed += " <-> ";
        }

        return stringed;
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof DoublyNode))
        {
            return false;
        }
        if(!(((DoublyNode<?>) o).value.equals(this.value)))
        {
            return false;
        }
        if(((DoublyNode<T>) o).getNext().isPresent())
        {
            Optional<DoublyNode<T>> thisOptional = DoublyNode.startOf(this), oOptional = DoublyNode.startOf(((DoublyNode<T>) o).getNext().get());
            DoublyNode<T> thisNext = thisOptional.orElse(null), oNext = oOptional.orElse(null);

            return thisNext.equals(oNext);
        }
        else
        {
            return true;
        }
    }

    @Override
    public Object clone()
    {
        DoublyNode<T> start = DoublyNode.startOf(this).orElse(null);
        DoublyNode<T> clone = new DoublyNode<>(start.value);

        while((start = start.next) != null)
        {
            clone.setNext(new DoublyNode<>(start.value));
            clone = clone.next;
        }

        return DoublyNode.startOf(clone).orElse(null);
    }

    @Override
    public int hashCode()
    {
        return DoublyLinkedList.from(this).length();
    }

}