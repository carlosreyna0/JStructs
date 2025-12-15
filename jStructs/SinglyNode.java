package jStructs;
import java.util.Optional;

/**
 * The node to be used in a {@code SinglyLinkedList}.
 * @author <a href="https://github.com/carlosreyna0">Carlos Reyna</a>
 */
public class SinglyNode<T>
{
    private T value;
    private SinglyNode<T> next;

    public SinglyNode(){}
    public SinglyNode(T value)
    {
        this.set(value);
    }

    /**
     * Sets the {@code SinglyNode}'s value.
     * @param value The value to replace the existing one with
     */
    public void set(T value)
    {
        this.value = value;
    }

    /**
     * Sets the {@code SinglyNode} connected to this one.
     * @param node The {@code SinglyNode} to connect (can be null).
     */
    public void setNext(SinglyNode<T> node)
    {
        this.next = node;
    }

    /**
     * @return An {@code Optional<SinglyNode<T>>} of the {@code SinglyNode} connected to this one.
     */
    public Optional<SinglyNode<T>> getNext()
    {
        return Optional.ofNullable(this.next);
    }

    /**
     * @return An {@code Optional<T>} containing the data stored in this node.
     */
    public Optional<T> get()
    {
        return Optional.ofNullable(this.value);
    }

    @Override
    public String toString()
    {
        SinglyNode<T> current = this;
        String toString = "(" + this.value.toString() + ")";

        while((current = current.next) != null)
        {
            toString += " -> " + "(" + current.value.toString() + ")";
        }

        return toString;
    }

    @Override
    public int hashCode()
    {
        return SinglyLinkedList.from(this).length();
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof SinglyNode))
        {
            return false;
        }

        SinglyNode<T> oNode = ((SinglyNode<T>) o), thisNode = this, oCurrent = oNode, thisCurrent = thisNode;
        int oLength = 1, thisLength = 1;

        while((oCurrent = oCurrent.next) != null)
        {
            oLength++;
        }
        while((thisCurrent = thisCurrent.next) != null)
        {
            thisLength++;
        }

        if(oLength != thisLength)
        {
            return false;
        }
        thisCurrent = thisNode;
        oCurrent = oNode;

        while((oCurrent = oCurrent.next) != null)
        {
            thisCurrent = thisCurrent.next;

            T oVal = oCurrent.get().isPresent() ?  oCurrent.get().get() : null;
            T thisVal = thisCurrent.get().isPresent() ? thisCurrent.get().get() : null;

            if(!oVal.equals(thisVal))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public Object clone()
    {
        SinglyNode<T> clone = new SinglyNode<>(this.get().isPresent() ? this.get().get() : null), current = this, cloneCurrent = clone;

        while((current = current.next) != null)
        {
            cloneCurrent.setNext(new SinglyNode<>(current.get().isPresent() ? current.get().get() : null));
            cloneCurrent = cloneCurrent.getNext().isPresent() ? cloneCurrent.getNext().get() : null;
        }
        return clone;
    }

}