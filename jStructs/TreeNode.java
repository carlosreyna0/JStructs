package jStructs;
import java.util.Optional;

public class TreeNode<T>
{
    public enum Type
    {
        ROOT,
        LEAF,
        BRANCH
    }

    private TreeNode<T> parent;
    private ArrList<TreeNode<T>, ?> children = new ArrList<>();
    private T value;

    public static <T> boolean contains(TreeNode<T> tree, TreeNode<T> node)
    {
        TreeNode<T>[] children = tree.getChildren();

        for(int i = 0; i < children.length; i++)
        {
            if(children[i].equals(node)) return true;

            if(contains(children[i], node))
            {
                return true;
            }

        }

        return false;
    }

    public void set(T value)
    {
        this.value = value;
    }

    public Optional<T> get()
    {
        return Optional.ofNullable(this.value);
    }

    public void setParent(TreeNode<T> parent)
    {
        this.parent = parent;
    }

    public Optional<TreeNode<T>> getParent()
    {
        return Optional.ofNullable((this.parent));
    }

    public void addChild(TreeNode<T> child)
    {
        this.children.add(child);
    }

    public TreeNode<T>[] getChildren()
    {
        return this.children.toArray();
    }

    public boolean isAncestorOf(TreeNode<T> node)
    {
        return TreeNode.contains(node, this);
    }

    public boolean isDescendantOf(TreeNode<T> node)
    {
        return TreeNode.contains(this, node);
    }

    public boolean isSiblingOf(TreeNode<T> node)
    {
        return node.getParent().equals(this.getParent());
    }

    public TreeNode<T>[] getAncestors()
    {
        return null;
    }

    public TreeNode<T>[] getSiblings()
    {
        TreeNode<T> parent = this.getParent().orElse(null);
        ArrList<TreeNode<T>, ?> children = ArrList.from(parent.getChildren());
        children.removeAll(this);

        return children.toArray();
    }

    public Type getType()
    {
        return Type.BRANCH;
    }
}