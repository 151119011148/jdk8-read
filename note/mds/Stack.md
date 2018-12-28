Stack简介

Stack是栈。它的特性是：先进后出(FILO, First In Last Out)。
java工具包中的Stack是继承于Vector(矢量队列)的，由于Vector是通过数组实现的，这就意味着，Stack也是通过数组实现的，而非链表。当然，我们也可以将LinkedList当作栈来使用。
Stack是线程安全的。建议在学习Stack之前先学习Vector类。

Stack结构

    public class Stack<E> extends Vector<E>

Vector结构

    public class Vector<E>
    extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable


Stack的部分源码
    
    package java.util;
    public
    class Stack<E> extends Vector<E> {
    // 版本ID。这个用于版本升级控制，这里不须理会！
    private static final long serialVersionUID = 1224463164541339165L;
    // 构造函数
    public Stack() {
    }
    // push函数：将元素存入栈顶
    public E push(E item) {
    // 将元素存入栈顶。
    // addElement()的实现在Vector.java中
    addElement(item);
    return item;
    }
    // pop函数：返回栈顶元素，并将其从栈中删除
    public synchronized E pop() {
    E obj;
    int len = size();
    obj = peek();
    // 删除栈顶元素，removeElementAt()的实现在Vector.java中
    removeElementAt(len - 1);
    return obj;
    }
    // peek函数：返回栈顶元素，不执行删除操作
    public synchronized E peek() {
    int len = size();
    if (len == 0)
    throw new EmptyStackException();
    // 返回栈顶元素，elementAt()具体实现在Vector.java中
    return elementAt(len - 1);
    }
    // 栈是否为空
    public boolean empty() {
    return size() == 0;
    }
    // 查找“元素o”在栈中的位置：由栈顶向下数
    public synchronized int search(Object o) {
    // 获取元素索引，elementAt()具体实现在Vector.java中
    int i = lastIndexOf(o);
    if (i >= 0) {
    return size() - i;
    }
    return -1;
    }
    }

关于方法public synchronized int search(Object o)有必要深入的跟进源码。
    
    public synchronized int search(Object o) {
    int i = lastIndexOf(o);
    if (i >= 0) {
    return size() - i;
    }
    return -1;
    }

跟进lastIndexOf(o);

    public synchronized int lastIndexOf(Object o) {
    return lastIndexOf(o, elementCount-1);
    }
    public synchronized int lastIndexOf(Object o, int index) {
    if (index >= elementCount)
    throw new IndexOutOfBoundsException(index + " >= "+ elementCount);
    if (o == null) {
    for (int i = index; i >= 0; i--)
    if (elementData[i]==null)
    return i;
    } else {
    for (int i = index; i >= 0; i--)
    if (o.equals(elementData[i]))
    return i;
    }
    return -1;
    }
可以看到，查找元素时，是从数组的尾部开始遍历的。


测试demo

    public class TestStack {
    public static void main(String[] args) {
    Stack s = new Stack();
    s.push("a");
    s.push("b");
    s.push("c");
    s.push("d");
    System.out.println(s.search("a"));
    }
    }
控制台输出：4
