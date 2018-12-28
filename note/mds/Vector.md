Vector源码介绍

全局变量

    /**
     * 保存vector中元素的数组。vector的容量是数组的长度，数组的长度最小值为vector的元素个数。
     * 任何在vector最后一个元素之后的数组元素是null。
     */
    protected Object[] elementData;
    
    /**
     * vector中实际的元素个数。
     */
    protected int elementCount;
    
    /**
     * vector需要自动扩容时增加的容量。
     * 当vector的实际容量elementCount将要大于它的最大容量时，vector自动增加的容量。
     * 如果capacityIncrement小于或等于0，vector的容量需要增长时将会成倍增长。
     */
    protected int capacityIncrement;



构造器
Vector有四个构造方法，其内部有两个重要的参数，一个是elementCount代表当前元素个数，一个是capacityIncrement，代表当列表元素满了之后增加的容量。如果不设置capacityIncrement，那么Vector容量扩展时默认将扩展两倍，在ArrayList源码分析中，我们知道ArrayList在扩容时默认将扩展1.5倍，所以这又是ArrayList与Vector的一个区别。
    
    public Vector(int initialCapacity, int capacityIncrement) {
            super();
            if (initialCapacity < 0)
                throw new IllegalArgumentException("Illegal Capacity: "+
                                                   initialCapacity);
            this.elementData = new Object[initialCapacity];
            this.capacityIncrement = capacityIncrement;
        }
    
    
    public Vector(int initialCapacity) {
            this(initialCapacity, 0);
        }
    
    
     public Vector() {
            this(10);
        }  
    
    
     public Vector(Collection<? extends E> c) {
            elementData = c.toArray();
            elementCount = elementData.length;
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
        }   
从上面代码还可以看到，Vector初始时容量为10，而ArrayList初始容量为0。


方法
copyInto

    /**
     * 将vector中的所有元素拷贝到指定的数组anArray中
     * @param  anArray 指定的数组anArray，用来存放vector中的所有元素
     * @throws NullPointerException 如果指定的数组anArray为null
     * @throws IndexOutOfBoundsException 如果指定的数组anArray的容量小于vector的元素个数
     * @throws ArrayStoreException 如果vector不能被拷贝到anArray中
     * @see #toArray(Object[])
     */
    public synchronized void copyInto(Object[] anArray) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }

trimToSize()

    /**
     * 将底层数组的容量调整为当前vector实际元素的个数，来释放空间。
     */
    public synchronized void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        ////当实际大小小于底层数组的长度
        if (elementCount < oldCapacity) {
            //将底层数组的长度调整为实际大小
            elementData = Arrays.copyOf(elementData, elementCount);
        }
    }
    
ensureCapacity
该方法的实现和ArrayList中大致相同。不同的是在第一次扩容时，vector的逻辑是： 
newCapacity = oldCapacity + ((capacityIncrement > 0) ? 
capacityIncrement : oldCapacity); 
即如果capacityIncrement>0，就加capacityIncrement，如果不是就增加一倍。 
而ArrayList的逻辑是： 
newCapacity = oldCapacity + (oldCapacity >> 1); 
即增加现有的一半。

    /**
     * 增加vector容量
     * 如果vector当前容量小于至少需要的容量，它的容量将增加。
     * 
     * 新的容量将在旧的容量的基础上加上capacityIncrement，除非capacityIncrement小于等于0，在这种情况下，容量将会增加一倍。
     *
     * 增加后，如果新的容量还是小于至少需要的容量，那就将容量扩容至至少需要的容量。
     *
     * @param minCapacity 至少需要的容量
     */
    public synchronized void ensureCapacity(int minCapacity) {
        if (minCapacity > 0) {
            modCount++;
            ensureCapacityHelper(minCapacity);
        }
    }
    
    
    /**
     * ensureCapacity()方法的unsynchronized实现。
     * ensureCapacity()是同步的，它可以调用本方法来扩容，而不用承受同步带来的消耗
     * @see #ensureCapacity(int)
     */
    private void ensureCapacityHelper(int minCapacity) {
        // 如果至少需要的容量 > 数组缓冲区当前的长度，就进行扩容
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
    
    /**
     * 分派给arrays的最大容量
     * 为什么要减去8呢？
     * 因为某些VM会在数组中保留一些头字，尝试分配这个最大存储容量，可能会导致array容量大于VM的limit，最终导致OutOfMemoryError。
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    /**
    * 扩容，保证vector至少能存储minCapacity个元素。
    * 首次扩容时，newCapacity = oldCapacity + ((capacityIncrement > 0) ?capacityIncrement : oldCapacity);即如果capacityIncrement>0，就加capacityIncrement，如果不是就增加一倍。
    * 如果第一次扩容后，容量还是小于minCapacity，就直接将容量增为minCapacity。
    * @param minCapacity 至少需要的容量
    */
    private void grow(int minCapacity) {
        // 获取当前数组的容量
        int oldCapacity = elementData.length;
        // 扩容。新的容量=当前容量+当前容量/2.即将当前容量增加一倍
        int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                         capacityIncrement : oldCapacity);
        //如果扩容后的容量还是小于想要的最小容量
        if (newCapacity - minCapacity < 0)
            ///将扩容后的容量再次扩容为想要的最小容量
            newCapacity = minCapacity;
        ///如果扩容后的容量大于临界值，则进行大容量分配
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    
    /**
     * 进行大容量分配
     */
    private static int hugeCapacity(int minCapacity) {
        //如果minCapacity<0，抛出异常
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        //如果想要的容量大于MAX_ARRAY_SIZE，则分配Integer.MAX_VALUE，否则分配MAX_ARRAY_SIZE
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }


添加操作
下面就以add(E e)方法为例介绍一个，其余方法与ArrayList中的流程类似。add()方法的实现如下：

    public synchronized boolean add(E e) {
            modCount++;
            //确保容量足够
            ensureCapacityHelper(elementCount + 1);
            //添加元素
            elementData[elementCount++] = e;
            return true;
        }
从上面代码可以看到，add()方法用synchronized关键字修饰，所以是线程安全的，ensureCapacityHelper()方法用于确保容量足够，不够时扩展容量，其实现如下：

    private void ensureCapacityHelper(int minCapacity) {
            // overflow-conscious code
            if (minCapacity - elementData.length > 0)
                grow(minCapacity);
        }
        
可以看到，当需要扩容时，将调用grow()方法。

    private void grow(int minCapacity) {
            // overflow-conscious code
            int oldCapacity = elementData.length;
            int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                             capacityIncrement : oldCapacity);
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity;
            if (newCapacity - MAX_ARRAY_SIZE > 0)
                newCapacity = hugeCapacity(minCapacity);
            elementData = Arrays.copyOf(elementData, newCapacity);
        }

可以看到，如果capacityIncrement为0时，那么newCapacity将会是两倍的oldCapacity，后面的操作与ArrayList中的相同。下面看一下ArrayList的grow方法可以看到区别。

    private void grow(int minCapacity) {
            // overflow-conscious code
            int oldCapacity = elementData.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity;
            if (newCapacity - MAX_ARRAY_SIZE > 0)
                newCapacity = hugeCapacity(minCapacity);
            // minCapacity is usually close to size, so this is a win:
            elementData = Arrays.copyOf(elementData, newCapacity);
        }

  从上面就可以看出两个类在得到newCapacity变量时的区别。 
  其他add方法与ArrayList相同，只不过每个方法多了synchronized关键字修饰。
  
  其余操作
  其余操作与ArrayList类似，只不过每个方法都多了synchronized关键字，从而保证了Vector类的线程安全。

        
总结

从源码中，我们不难论证以前对Vector的了解

Vector底层是数组。

有序。Vector底层是数组，数组是有序的。

可重复。数组是可重复的。

随机访问效率高，增删效率低。通过索引可以很快的查找到对应元素，而增删元素许多元素的位置都要改变。

线程安全。很多方法都是synchronized的。