package com.example.sourcecode.study.Self.jdk8;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Create by  GF  in  15:35 2019/1/14
 * Description:
 * Modified  By:
 */
public class StreamTest {
    /**
     * java.util.Stream 表示能应用在一组元素上一次执行的操作序列。
     * Stream 操作分为中间操作或者最终操作两种，最终操作返回一特定类型的计算结果，
     * 而中间操作返回Stream本身，这样你就可以将多个操作依次串起来(链式编程)。
     * Stream 的创建需要指定一个数据源，比如 java.util.Collection的子类，List或者Set， Map不支持。
     * Stream的操作可以串行执行或者并行执行。
     * Stream 作为 Java 8 的一大亮点，它与 java.io 包里的 InputStream 和 OutputStream 是完全不同的概念。
     * Java 8 中的 Stream 是对集合（Collection）对象功能的增强，它专注于对集合对象进行各种非常便利、
     * 高效的聚合操作（aggregate operation），或者大批量数据操作 (bulk data operation)。
     * Stream API 借助于同样新出现的Lambda表达式，极大的提高编程效率和程序可读性。
     * 同时它提供串行和并行两种模式进行汇聚操作
     */

    @Test
    public void testStream1() {
        // 1.使用值构建
        Stream<String> stream = Stream.of("a", "b", "c");
        // 2. 使用数组构建
        String[] strArray = new String[]{"a", "b", "c"};
        Stream<String> stream1 = Stream.of(strArray);
        Stream<String> stream2 = Arrays.stream(strArray);
        // 3. 利用集合构建(不支持Map集合)
        List<String> list = Arrays.asList(strArray);
        stream = list.stream();
/*        对于基本数值型，目前有三种对应的包装类型 Stream：IntStream、LongStream、DoubleStream。
        当然我们也可以用 Stream<Integer>、Stream<Long> 、Stream<Double>，但是 自动拆箱装箱会很耗时，所以特别为这三种基本数值型提供了对应的 Stream。
        Java 8 中还没有提供其它基本类型数值的Stream*/
    }


    //数值Stream的构建:
    //Stream转换为其它类型:
    @Test
    public void testStream2() {
        IntStream stream1 = IntStream.of(new int[]{1, 2, 3});

        IntStream stream2 = IntStream.range(1, 3);//[1,3)

        IntStream stream3 = IntStream.rangeClosed(1, 3);//[1,3]

        Stream<String> stream = Stream.of("hello", "world", "tom");
// 1. 转换为Array
        String[] strArray = stream.toArray(String[]::new);
// 2. 转换为Collection
        List<String> list1 = stream.collect(Collectors.toList());
        List<String> list2 = stream.collect(Collectors.toCollection(ArrayList::new));
        Set<String> set3 = stream.collect(Collectors.toSet());
        Set<String> set4 = stream.collect(Collectors.toCollection(HashSet::new));
// 3. 转换为String
        String str = stream.collect(Collectors.joining()).toString();
/*        特别注意 : 一个 Stream 只可以使用一次，上面的代码为了简洁而重复使用了多次。
        这个代码直接运行会抛出异常的:
        java.lang.IllegalStateException: stream has already been operated upon or closed*/
    }

    //Stream操作

    /**
     * Intermediate：中间操作,
     * Terminal： 最终操作,
     * Short-circuiting： 短路操作
     * map/flatMap映射
     */
    @Test
    public void testOperate() {
 /*       当把一个数据结构包装成Stream后，就要开始对里面的元素进行各类操作了。常见的操作可以归类如下。

        Intermediate：中间操作
        map (mapToInt, flatMap 等)、 filter、 distinct、 sorted、 peek、 limit、 skip、 parallel、 sequential、 unordered

        Terminal： 最终操作
        forEach、 forEachOrdered、 toArray、 reduce、 collect、 min、 max、 count、 anyMatch、 allMatch、 noneMatch、findFirst、 findAny、 iterator

        Short-circuiting： 短路操作
        anyMatch、 allMatch、 noneMatch、 findFirst、 findAny、 limit

        map/flatMap映射 把 Stream中 的每一个元素，映射成另外一个元素。*/

//转换大写
        Stream<String> wordList = Stream.of("hello", "world", "tom");
        List<String> output = wordList.map(String::toUpperCase).collect(Collectors.toList());
        //也可以直接使用forEach循环输出
        wordList.map(String::toUpperCase).collect(Collectors.toList()).forEach(System.out::println);


//计算平方数
        List<Integer> nums = Arrays.asList(1, 2, 3, 4);
        List<Integer> squareNums = nums.stream().map(n -> n * n).collect(Collectors.toList());

/*        map生成的是个1:1映射，每个输入元素，都按照规则转换成为另外一个元素。还有一些场景，是一对多映射关系的，这时需要 flatMap。
        map和flatMap的方法声明是不一样的
                <R> Stream<R>               map(Function<? super T, ? extends R> mapper);
<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);*/

//stream1中的每个元素都是一个List集合对象
        Stream<List<Integer>> stream1 = Stream.of(
                Arrays.asList(1),
                Arrays.asList(2, 3),
                Arrays.asList(4, 5, 6)
        );
        Stream<Integer> stream2 = stream1.
                flatMap((e) -> e.stream());

        stream2.forEach(e -> System.out.println(e));//输出1 2 3 4 5 6
        // flatMap 把 stream1 中的层级结构扁平化，就是将最底层元素抽出来放到一起，最终新的 stream2 里面已经没有 List 了，都是直接的数字。

        //例子:
        Stream<String> stream = Stream.of("tom.Li", "lucy.Liu");
//flatMap方法把stream1中的每一个字符串都用[.]分割成了俩个字符串
//最后返回了一个包含4个字符串的stream2
        Stream<String> stream4 = stream.flatMap(s -> Stream.of(s.split("[.]")));
        stream2.forEach(System.out::println);
/*        输出结果:
         tom
         Li
         lucy
         Liu
        forEach 遍历 接收一个 Lambda 表达式，然后在 Stream 的每一个元素上执行该表达式。
        forEach 是 terminal 操作，执行完stream就不能再用了*/

        List<String> list = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        list.stream().forEach(System.out::println);
       /* filter 过滤 对原始 Stream 进行某项测试，通过测试的元素被留下来生成一个新 Stream。
        通过一个predicate接口来过滤并只保留符合条件的元素，该操作属于中间操作，所以我们可以在过滤后的结果来应用其他Stream操作（比如forEach）。forEach需要一个函数来对过滤后的元素依次执行。forEach是一个最终操作，所以我们不能在forEach之后来执行其他Stream操作
*/
        List<String> list2 = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        list2.stream().filter(s -> s.length() > 4).forEach(System.out::println);
/*        注意:System.out::println 这个是lambda表达式中对静态方法的引用
        peek 对每个元素执行操作并返回一个新的 Stream
        注意:调用peek之后,一定要有一个最终操作
        peek是一个intermediate 操作*/

        List<String> list3 = Arrays.asList("one", "two", "three", "four");
        List<String> list4 = list3.stream()
                .filter(e -> e.length() > 3)
                .peek(e -> System.out.println("第一次符合条件的值为: " + e))
                .filter(e -> e.length() > 4)
                .peek(e -> System.out.println("第二次符合条件的值为: " + e))
                .collect(Collectors.toList());
        System.out.println(list3.size());//打印结果为 1
/*        最后list2中就存放的筛选出来的元素
        findFirst 总是返回 Stream 的第一个元素，或者空，返回值类型：Optional。
        如果集中什么都没有,那么list.stream().findFirst()返回一个Optional<String>对象,但是里面封装的是一个null。*/

        List<String> list5 = Arrays.asList("test", "hello", "world");
        Optional<String> first = list5.stream().findFirst();
        System.out.println(first.orElse("值为null"));

    }

    //sort 排序

    /**
     * 排序是一个中间操作，返回的是排序好后的Stream。如果你不指定一个自定义的Comparator则会使用默认排序。
     * 对 Stream 的排序通过 sorted 进行，它比数组的排序更强之处在于你可以首先对 Stream 进行各类
     * map、filter、limit、skip 甚至 distinct 来减少元素数量后，再排序，这能帮助程序明显缩短执行时间。
     */
    @Test
    public void testSort() {
        List<String> list = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        list.stream().sorted().filter(s -> s.startsWith("j")).forEach(System.out::println);
        //按照字符串的长短排序
        list.stream().sorted((s1, s2) -> s1.length() - s2.length()).forEach(System.out::println);
        //需要注意的是，排序只创建了一个排列好后的Stream，而不会影响原有的数据源，排序之后原数据list是不会被修改的

    }


    /*map 映射
    中间操作map会将元素根据指定的Function接口来依次将元素转成另外的对象，
    下面的示例展示了将字符串转换为大写字符串。
    你也可以通过map来讲对象转换成其他类型，map返回的Stream类型是根据你map传递进去的函数的返回值决定的。
   */
    @Test
    public void testMap() {
        List<String> list = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        list.stream().map(s -> s.toUpperCase()).forEach(System.out::println);
    }

    /*Match 匹配
    Stream提供了多种匹配操作，允许检测指定的Predicate是否匹配整个Stream。所有的匹配操作都是最终操作，并返回一个boolean类型的值。
  */
    @Test
    public void testMatch() {
//所有元素匹配成功才返回true 否则返回false

        List<String> list = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        boolean allMatch = list.stream().allMatch((s) -> s.startsWith("j"));
        System.out.println(allMatch);

//任意一个匹配成功就返回true 否则返回false

        List<String> list1 = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        boolean anyMatch = list1.stream().anyMatch((s) -> s.startsWith("j"));
        System.out.println(anyMatch);

//没有一个匹配的就返回true 否则返回false

        List<String> list2 = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        boolean noneMatch = list2.stream().noneMatch((s) -> s.startsWith("j"));
        System.out.println(noneMatch);

    }

    /*Count 计数
    计数是一个最终操作，返回Stream中元素的个数，返回值类型是long。
    */
    @Test
    public void testCount() {
        List<String> list = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        long count = list.stream().filter(s -> s.startsWith("j")).count();
        System.out.println(count);
    }

    /*Reduce 规约/合并
        这是一个最终操作，允许通过指定的函数来将stream中的多个元素规约合并为一个元素.
        它提供一个起始值（种子），然后依照运算规则（BinaryOperator），
        和前面 Stream 的第一个、第二个、第 n 个元素组合。Stream.reduce，
        常用的方法有average, sum, min, max, and count，返回单个的结果值，
        并且reduce操作每处理一个元素总是创建一个新值.
        从这个意义上说，字符串拼接、数值的 sum、min、max等都是特殊的 reduce。   
    */
    @Test
    public void testReduce() {
        IntStream integers = IntStream.range(1, 10);
        Integer sum = integers.reduce(0, (a, b) -> a + b);
        //或Integer sum = integers.reduce(0, Integer::sum);
        //也有没有起始值的情况，这时会把 Stream 的前面两个元素组合起来，返回的是 Optional。
        OptionalInt min = integers.reduce((a, b) -> a < b ? a : b);
        // 字符串连接，concat = "ABCD"
        String concat = Stream.of("A", "B", "C", "D").reduce("", String::concat);
        Optional<String> opStr = Stream.of("A", "B", "C", "D").reduce(String::concat);

        List<String> list = Arrays.asList("test", "javap", "hello", "world", "java", "tom", "C", "javascript");
        Optional<String> reduce = list.stream()
                .sorted((s1, s2) -> s2.length() - s1.length())
                .filter(s -> s.startsWith("j"))
                .map(s -> s + "_briup")
                .reduce((s1, s2) -> s1 + "|" + s2);
        System.out.println(reduce.orElse("值为空"));//打印结果为: javascript_briup|javap_briup|java_briup
/*      1.先调用stream方法
      2.再排序，按照字符串的长度进行排序，长的在前短的再后
      3.再过滤，字符串必须是以字符'j'开头的
      4.再进行映射，把每个字符串后面拼接上"_briup"
      5.再调用reduce进行合并数据,使用"|"连接字符串
      6.最后返回Optional<String>类型数据，处理好的字符串数据就封装在这个对象中      */

    }

    /**
     * limit 返回 Stream 的前面 n 个元素；skip 则是跳过前 n 个元素只要后面的元素
     */
    @Test
    public void testLimitAndSkip() throws FileNotFoundException {
        List<String> list = Arrays.asList("test", "javap", "hello", "world", "java", "tom", "C", "javascript");
        list.stream().limit(5).forEach(System.out::println);
        list.stream().skip(5).forEach(System.out::println);


//找出字符文件中字符字符最长的一行
        BufferedReader br = new BufferedReader(new FileReader("src/com/briup/test/a.txt"));
        int maxLen = br.lines().
                mapToInt(String::length).
                max().
                getAsInt();

        System.out.println(maxLen);
        //注意:lines方法把文件中所有行都返回并且转换为一个Stream<String>类型对象,因为每行读出的String类型数据,同时String::length是使用方法引用的特殊方式(因为泛型的缘故),上面的笔记中已经介绍过了,max()方法执行后返回的时候OptionalInt类型对象,所以接着调用了getAsInt方法来获得这次运行结果的int值

//找出全文的单词，转小写，去掉空字符,去除重复单词并排序
        br = new BufferedReader(new FileReader("src/com/briup/test4/day17.txt"));
        br.lines().
                flatMap(s -> Stream.of(s.split(" "))).
                filter(s -> s.length() > 0).
                map(String::toLowerCase).
                distinct().
                sorted().
                forEach(System.out::println);

    }


    /**
     * 通过Supplier接口，可以自己来控制Stream的生成。这种情形通常用于随机数、常量的 Stream，
     * 或者需要前后元素间维持着某种状态信息的 Stream。
     * 把 Supplier 实例传递给 Stream.generate() 生成的 Stream，由于它是无限的，在管道中，
     * 必须利用limit之类的操作限制Stream大小。可以使用此方式制造出海量的测试数据
     */
    @Test
    public void testStreamGenerate() {
        //public static<T> Stream<T> generate(Supplier<T> s);
        //生成100个随机数并由此创建出Stream实例
        Stream.generate(() -> (int) (Math.random() * 100)).limit(100).forEach(System.out::println);

    }

    /**
     * Stream.iterate
     * iterate 跟 reduce 操作很像，接受一个种子值，和一个 UnaryOperator（假设是 f）。
     * 然后种子值成为 Stream 的第一个元素，f(seed) 为第二个，f(f(seed)) 第三个，
     * f(f(f(seed))) 第四个,以此类推。
     */
    @Test
    public void testIterate() {
        //生成一个等差数列
        Stream.iterate(0, n -> n + 3).
                limit(10).
                forEach(x -> System.out.print(x + " "));
    }

    /**
     * Collectors
     * java.util.stream.Collectors 类的主要作用就是辅助进行各类有用的操作。
     * 例如把Stream转变输出为 Collection，或者把 Stream 元素进行分组。
     */
    @Test
    public void testCollectors() {
//把Stream中的元素进行过滤然后再转为List集合
        List<String> list = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
        List<String> result = list.stream().filter(s -> s.length() > 4).collect(Collectors.toList());

//分组:按照字符串的长度分组
        List<String> list2 = Arrays.asList("test", "hello", "world", "java", "tom", "C", "javascript");
//相同长度的字符串放到一个List集合中作为Map的value,字符串的长度作为Map的Key
        Map<Integer, List<String>> collect = list2.stream().collect(Collectors.groupingBy(String::length));
//注意下面写法可能写到s->s.length()的时候Eclipse里面有可能不会代码提示，这个要看你先的是=号的哪一边
//最终原因还是泛型的事情
        Map<Integer, List<String>> collect2 = list2.stream().collect(Collectors.groupingBy(s -> s.length()));

//分割:按照字符串是否包含java进行划分    partitioning分割划分的意思
        Map<Boolean, List<String>> collect3 =
                list.stream().collect(Collectors.partitioningBy(s -> s.indexOf("java") != -1));
        for (Boolean b : collect3.keySet()) {
            System.out.println(b + " : " + collect3.get(b).size());
        }
    }

    /**
     * 并行Streams
     * Stream有串行和并行两种，串行Stream上的操作是在一个线程中依次完成，
     * 而并行Stream则是在多个线程上同时执行。
     */
    @Test
    public void testParallelStream() {
        //生成100万个不同的字符串放到集合中
        int max = 1000000;
        List<String> values = new ArrayList<String>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }


        //1纳秒*10^9=1秒
        long t0 = System.nanoTime();
        //串行stream
        long count = values.stream().sorted().count();
        //并行stream
        //long count = values.parallelStream().sorted().count();
        long t1 = System.nanoTime();

        long time = t1 - t0;
        System.out.println(count);
        System.out.println(time);
    }
//结论:对100万个字符串进行排序和计数操作，串行和并行运算的用时差别还是很明显的


    /**
     * Map类型不支持stream，不过Map提供了一些新的有用的方法来处理一些日常任务。
     */
    @Test
    public void testJDKMap() {
        /**
         *  Java8为Map新增的方法：
         *  Object compute(Object key, BiFunction remappingFunction);
         该方法使用remappingFunction根据原key-value对计算一个新的value。
         只要新的value不为null，就使用新的value覆盖原value；如果新的value为null，则删除原key-value对；
         Object computeIfAbsent(Object key, Function mappingFunction):
         如果传入的key参数在Map中对应的value为null，
         该方法将使用mappingFunction根据原key、value计算一个新的结果，则用该计算结果覆盖原value；
         如果传入的key参数在Map中对应的value为null，则该方法不做任何事情；如果原Map原来不包括该key，
         该方法可能会添加一组key-value对。
         Object computeIfPresent(Object key, BiFunction remappingFunction):
         如果传给该方法的key参数在Map中对应的value不为null，
         该方法将使用remappingFunction根据原key、value计算一个新结果，并且该计算结果不为null，
         则使用该结果覆盖原来的value；
         如果计算结果为null，则删除原key-value对。
         void forEach(BiConsumer action):
         该方法是Java8为Map新增的一个遍历key-value对的方法。
         Object getOrDefault(Object key, V defaultValue):
         获取指定的key对应的value。如果该key不存在，则返回defaultValue。
         Object merge(Object key, Object value, BiFunction remappingFunction):
         该方法会先根据key参数获取该Map中对应的value。如果获取的value为null，
         则直接使用传入的value覆盖原value（在这种情况下，可能会添加一组key-value）；
         如果获取的value不为null，则使用remappingFunction函数根据原value、新value计算一个新的结果，并用新的结果去覆盖原有的value。
         Object putIfAbsent(Object key, Object value):
         该方法会自动检测指定的key对应的value是否为null，如果该key对应的value为null，则使用传入的新value代替原来的null。
         如果该key对应的value不是null，那么该方法不做任何事情。
         Object replace(Object key, Object value):
         将Map中指定key对应的value替换成新value并把被替换掉的旧值返回。
         如果key在Map中不存在，该方法不会添加key-value对，而是返回null。
         Boolean replace(K key, V oldValue, V newValue):
         将Map中指定的key-value对的原value替换成新value。
         如果在Map中找到指定的key-value对，则执行替换并返回true，否则返回false。
         replaceAll(BiFunction function):
         该方法使用function对原key-value对执行计算，并将计算结果作为key-value对的value值
         *
         */


    }


}