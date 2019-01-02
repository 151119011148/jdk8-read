一、前言

　　今天开始阅读jdk1.8的集合部分，平时在写项目的时候，用到的最多的部分可能就是Java的集合框架，通过阅读集合框架源码，了解其内部的数据结构实现，能够深入理解各个集合的性能特性，并且能够帮助自己在今后的开发中避免犯一些使用错误。另外笔者自己也是摸着石头过河，如果有描述不当的地方，希望园友们能够不吝指出，希望能够和大家共同进步！


二、集合框架概览图

![Collection](../images/CollectionUml.png)

　　可以看到集合的基础接口是Map, Collection以及Iterator。其余的类都实现自这3个类。

　　蓝色为接口，红色为类，绿色为抽象类。
空心三角形虚线：实现接口（implements)，好像也不太准确，列如list和collection的关系是extends。因为list是接口
空心三角形实线：继承（extends）

三、基础接口的源码解析
3.1 Iterator接口
```java
public interface Iterator<E> {
      boolean hasNext();
      E next();
      default void remove() {
        throw new UnsupportedOperationException("remove");
      }
      /**
      * 举个简单例子（把集合里的元素每个都输出出来）：
      * List<String> names = new ArrayList<>();
      * names.add("Joemsu");
      * names.add("GodnessY");
      * names.iterator().forEachRemaining(c -> System.out.println("hi! " + c));
      */
      default void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        while (hasNext())
          action.accept(next());
      }
    }
```

　　可以看到Iterator接口还是很简单的，做到了见名知意，值得一提的是里面的remove方法：此方法可用于在迭代中删除结合中的元素，如果不用Iterator，在list循环中使用remove会抛异常。另外forEachRemaining()给出了简单的例子，里面的Consumer函数式接口有空再具体讲解。


3.2 Collection接口
```java
 public interface Collection<E> extends Iterable<E> {
      //值得一提的是:如果size超过Integer.MAX_VALUE也只会返回Integer.MAX_VALUE
      int size();
      boolean isEmpty();
      //如果向集合里添加null，使用contains(null)，也可以返回true
      boolean contains(Object o);
      Iterator<E> iterator();
      /**
       * 深层拷贝，修改数组的数据不会对集合里的元素产生影响。
       * 注意：只能返回Object[],不能强制转换其他类型，如需要转型，使用下面带泛型的方法。
       */
      Object[] toArray();
      <T> T[] toArray(T[] a);
      boolean add(E e);
      boolean remove(Object o);
      boolean containsAll(Collection<?> c);
      boolean addAll(Collection<? extends E> c);
      boolean removeAll(Collection<?> c);
      //保留c集合里的元素
      boolean retainAll(Collection<?> c);
      void clear();
      boolean equals(Object o);
      //如过a.equals(b)，则hashCode()肯定相同，反之不一定
      int hashCode();
      
      //针对parallelStream()添加的方法，用于分割集合，进行并行处理
      @Override
      default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, 0);
      }
      
      /**
      * 如果满足filter，则删除，举个栗子：
      * Collection<String> myHeart = new ArrayList<>();
      * myHeart.add("Boduolaoshi");
      * myHeart.add("GodnessY");
      * System.out.println("before: " + myHeart.size());
      * myHeart.removeIf(s -> s.equals("Boduolaoshi"));
      * System.out.println("after: " + myHeart.size());
      */
      default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
          if (filter.test(each.next())) {
            each.remove();
            removed = true;
          }
        }
        return removed;
      }
      
      default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
      }
      
      //采用并行处理，使用多核cpu的特性
      default Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
      }
    }
```
   
　　需要注意的一些地方已经在注释这里特别说明过了，另外对于spliterator()，不是特别清楚的园友们，可以点击查看这里，回答的已经很详细了。


3.3 Map接口
    
```java
public interface Map<K,V> {
      //同样的，如果size超过Integer.MAX_VALUE也只会返回Integer.MAX_VALUE
      int size();
      boolean isEmpty();
      boolean containsKey(Object key);
      boolean containsValue(Object value);
      V get(Object key);
      V put(K key, V value);
      V remove(Object key);
      void putAll(Map<? extends K, ? extends V> m);
      void clear();
      //对set的变动会影响到map，反过来也一样
      Set<K> keySet();
      //对Collection的变动会影响到map，反过来也一样
      Collection<V> values();
      //对Set的变动会影响到map，反过来也一样
      Set<Map.Entry<K, V>> entrySet();
      boolean equals(Object o);
      int hashCode();
      
      //Entry start
      interface Entry<K,V> {
        K getKey();
        V getValue();
        V setValue(V value);
        boolean equals(Object o);
        int hashCode();
        //使用默认方法对Key进行比较
        public static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K,V>> comparingByKey(){
          return (Comparator<Map.Entry<K, V>> & Serializable)
            (c1, c2) -> c1.getKey().compareTo(c2.getKey());
        }
        //使用默认方法对Value比较
        public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K,V>> comparingByValue() {
          return (Comparator<Map.Entry<K, V>> & Serializable)
            (c1, c2) -> c1.getValue().compareTo(c2.getValue());
        }
        /**
        * 自己传比较的方法，举个栗子：
        * Map<String, Integer> map = new HashMap<>();
        * map.put("sorted", 2);
        * map.put("collect", 1);
        * map.put("each", 3);
        * System.out.println("before sort");
        * map.entrySet().forEach(System.out::println);
        * System.out.println("after sort");
        * map.entrySet()
        *     .stream()
        *     .sorted(Map.Entry.comparingByKey((a, b) -> a.length() - b.length()))
        *     .collect(Collectors.toList()).forEach(System.out::println);
        */
        public static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
          Objects.requireNonNull(cmp);
          return (Comparator<Map.Entry<K, V>> & Serializable)
            (c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
        }
        public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
          Objects.requireNonNull(cmp);
          return (Comparator<Map.Entry<K, V>> & Serializable)
            (c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
        }
      }
      //Entry end
      
      //获取指定key 的value，没有则返回默认值
      default V getOrDefault(Object key, V defaultValue) {
        V v;
        return (((v = get(key)) != null) || containsKey(key))
          ? v
          : defaultValue;
      }
      
      /**
      * 对每队键值对操作： map.forEach((i, j) -> System.out.println(i + j))
      * 注意这里的(i, j)的类型与你初始化map的键值类型对应，i即K, j即V 
      */
      default void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<K, V> entry : entrySet()) {
          K k;
          V v;
          try {
            k = entry.getKey();
            v = entry.getValue();
          } catch(IllegalStateException ise) {
            // this usually means the entry is no longer in the map.
            throw new ConcurrentModificationException(ise);
          }
          action.accept(k, v);
        }
      }
      
      /**
      * 传入BiFunction类型，对每个键值对进行处理，返回类型与V类型相同
      * Map<String, Integer> map = new HashMap<>();
      * map.put("hi", 3);
      * map.put("hello", 4);
      * BiFunction<String, Integer, Integer> bi = (a, b) -> a.length() + b; //为了容易理解，这么写
      * map.forEach((i, j) -> System.out.println(i + ":" + j));
      * map.replaceAll(bi);
      * map.forEach((i, j) -> System.out.println(i + ":" + j));
      */
      default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        for (Map.Entry<K, V> entry : entrySet()) {
          K k;
          V v;
          try {
            k = entry.getKey();
            v = entry.getValue();
          } catch(IllegalStateException ise) {
            // this usually means the entry is no longer in the map.
            throw new ConcurrentModificationException(ise);
          }
    
          // ise thrown from function is not a cme.
          v = function.apply(k, v);
    
          try {
            entry.setValue(v);
          } catch(IllegalStateException ise) {
            // this usually means the entry is no longer in the map.
            throw new ConcurrentModificationException(ise);
          }
        }
      }
      
      //如果为空的话，插入
      default V putIfAbsent(K key, V value) {
        V v = get(key);
        if (v == null) {
          v = put(key, value);
        }
    
        return v;
      }
      
      default boolean remove(Object key, Object value) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, value) ||
            (curValue == null && !containsKey(key))) {
          return false;
        }
        remove(key);
        return true;
      }
      
      default boolean replace(K key, V oldValue, V newValue) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, oldValue) ||
            (curValue == null && !containsKey(key))) {
          return false;
        }
        put(key, newValue);
        return true;
      }
      
      default V replace(K key, V value) {
        V curValue;
        if (((curValue = get(key)) != null) || containsKey(key)) {
          curValue = put(key, value);
        }
        return curValue;
      }
      
      //如果key不存在，则通过mappingFunction生成value，并插入
      default V computeIfAbsent(K key,
                Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
          V newValue;
          if ((newValue = mappingFunction.apply(key)) != null) {
            put(key, newValue);
            return newValue;
          }
        }
    
        return v;
      }
      
      //如果存在key对应的值，则通过remappingFunction来计算新的value，（value不为空）然后更新，为空则删除key
      default V computeIfPresent(K key,
                BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if ((oldValue = get(key)) != null) {
          V newValue = remappingFunction.apply(key, oldValue);
          if (newValue != null) {
            put(key, newValue);
            return newValue;
          } else {
            remove(key);
            return null;
          }
        } else {
          return null;
        }
      }
      
      default V compute(K key,
                BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);
    
        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
          // delete mapping
          if (oldValue != null || containsKey(key)) {
            // something to remove
            remove(key);
            return null;
          } else {
            // nothing to do. Leave things as they were.
            return null;
          }
        } else {
          // add or replace old mapping
          put(key, newValue);
          return newValue;
        }
      }
      
      //将旧的oldValue和新的传进去value通过remappingFunction进行处理，然后更新
      default V merge(K key, V value,
                BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(key);
        V newValue = (oldValue == null) ? value :
        remappingFunction.apply(oldValue, value);
        if(newValue == null) {
          remove(key);
        } else {
          put(key, newValue);
        }
        return newValue;
      }
    }
```
    
　　那么Map的接口源码阅读就到这里。


四、总结

　　总的来说，jdk1.8集合与之前版本不同的地方就是加入了很多default方法，以及使用了各种函数型接口，但总体来说还是比较好理解的。