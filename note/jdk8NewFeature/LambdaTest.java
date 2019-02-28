package com.example.sourcecode.study.Self.jdk8;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LambdaTest {

    public static void main(String[] args) {
            //test1();
            //test2();
            //test3();
            //test4();
            //test5();
            //test6();
            //test7();
            test8();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("");
                }
            });

            new Thread(() -> System.out.println(""));

    }


        /**
         *forEach()  方法，它可以迭代所有对象，并将你的lambda代码应用在其中。
         */
    private static void test1() {
        // Java 8之前：
        List<String> features = Arrays.asList("Lambdas", "Default Method", "Stream API", "Date and Time API");
        for (String feature : features) {
            System.out.println(feature);
        }
        // Java 8之后：
        // 使用Java 8的方法引用更方便，方法引用由::双冒号操作符标示，
        // 看起来像C++的作用域解析运算符
        features.forEach(System.out :: println);
    }

    /**
     * 下面是Java 8 Predicate 的例子，
     * 展示了过滤集合数据的多种常用方法。Predicate接口非常适用于做过滤。
     */
    private static void test2(){
        List languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");

        System.out.println("Languages which starts with J :");
        filter(languages, (str)->str.toString().startsWith("J"));

        System.out.println("Languages which ends with a ");
        filter(languages, (str)->str.toString().endsWith("a"));

        System.out.println("Print all languages :");
        filter(languages, (str)->true);

        System.out.println("Print no language : ");
        filter(languages, (str)->false);

        System.out.println("Print language whose length greater than 4:");
        filter(languages, (str)->str.toString().length() > 4);
        Predicate condition = (str) -> str.toString().length() > 4;
        languages.stream().filter(name -> condition.test(name)).forEach(System.out::println);

        /**
         * 允许将两个或更多的 Predicate 合成一个。它提供类似于逻辑操作符AND和OR的方法，
         * 名字叫做and()、or()和xor()，用于将传入 filter() 方法的条件合并起来。例如，要得到所有以J开始，
         * 长度为四个字母的语言，可以定义两个独立的 Predicate 示例分别表示每一个条件，
         * 然后用 Predicate.and() 方法将它们合并起来，如下所示：
         */
        Predicate<String> startsWithJ = (n) -> n.startsWith("J");
        Predicate<String> fourLetterLong = (n) -> n.length() == 4;
        languages.stream()
                .filter(startsWithJ.and(fourLetterLong))
                .forEach((n) -> System.out.print("nName, which starts with 'J' and four letter long is : " + n));
    }
    /**
     * 除了在语言层面支持函数式编程风格，Java 8也添加了一个包，叫做 java.util.function。
     * 它包含了很多类，用来支持Java的函数式编程。其中一个便是Predicate，使用 java.util.function.Predicate 函数式接口
     * 以及lambda表达式，可以向API方法添加逻辑，用更少的代码支持更多的动态行为。
     * @param names
     * @param condition
     */
    private static void filter(List<String> names, Predicate condition) {
/*        for(String name: names)  {
            if(condition.test(name)) {
                System.out.println(name + " ");
            }
        }*/
        names.stream().filter(name -> condition.test(name)).forEach(System.out::println);
    }

    /**
     * 本例介绍最广为人知的函数式编程概念map。它允许你将对象进行转换。
     * 例如在本例中，我们将 costBeforeTax 列表的每个元素转换成为税后的值。
     * 我们将 x -> x*x lambda表达式传到 map() 方法，后者将其应用到流中的每一个元素。
     * 然后用 forEach() 将列表元素打印出来。使用流API的收集器类，可以得到所有含税的开销。
     * 有 toList() 这样的方法将 map 或任何其他操作的结果合并起来。
     * 由于收集器在流上做终端操作，因此之后便不能重用流了。
     */
    private static void test3(){
        // 不使用lambda表达式为每个订单加上12%的税
        Double total1 = 0D;
        List<Integer> costBeforeTax = Arrays.asList(100, 200, 300, 400, 500);
        for (Integer cost : costBeforeTax) {
            double price = cost + .12*cost;
            total1 = total1 + price;
            System.out.println(price);
        }
        System.out.println("Total1 : " + total1);
        // 使用lambda表达式
        costBeforeTax.stream().map(cost ->  cost + cost * 0.12).forEach(System.out::println);
        Double total2 = costBeforeTax.stream().map(cost -> cost + cost * 0.12).reduce((cost,sum) -> sum + cost).get();
        System.out.println("Total2 : " + total2);
    }
    /**
     * 过滤是Java开发者在大规模集合上的一个常用操作，而现在使用lambda表达式和流API
     * 过滤大规模数据集合是惊人的简单。流提供了一个 filter() 方法，接受一个 Predicate 对象，
     * 即可以传入一个lambda表达式作为过滤逻辑。下面的例子是用lambda表达式过滤Java集合，将帮助理解。
     */
    private static void test4(){
        // 创建一个字符串列表，每个字符串长度大于2
        List<String> strList = Arrays.asList("abc","" , "bcd","" , "defg", "jk");
        List<String> filtered = strList.stream().filter(x -> x.length()> 2).collect(Collectors.toList());
        //%s,打印字符串 %n换行
        System.out.printf("Original List : %s%n, filtered list : %s", strList, filtered);
    }
    /**
     * 我们通常需要对列表的每个元素使用某个函数，例如逐一乘以某个数、
     * 除以某个数或者做其它操作。这些操作都很适合用 map() 方法，
     * 可以将转换逻辑以lambda表达式的形式放在 map() 方法里，就可以对集合的各个元素进行转换了，如下所示。
     */
    private static void test5(){
        List<String> G7 = Arrays.asList("USA", "Japan", "France", "Germany", "Italy", "U.K.","Canada");
        String G7String = G7.stream().map(x -> x.toUpperCase()).collect(Collectors.joining("*********"));
        System.out.println(G7String);
    }

    /**
     * 展示了如何利用流的 distinct() 方法来对集合进行去重
     */
    private static void test6(){
        // 用所有不同的数字创建一个正方形列表
        List<Integer> numbers = Arrays.asList(9, 10, 3, 4, 7, 3, 4);
        List<Integer> distinct = numbers.stream().map( i -> i*i).distinct().collect(Collectors.toList());
        List<Integer> distinct2 = numbers.stream().distinct().map( i -> i*i).collect(Collectors.toList());
        System.out.printf("Original List : %s,  Square Without duplicates : %s %n", numbers, distinct2);
    }
    /**
     * IntStream、LongStream 和 DoubleStream 等流的类中，有个非常有用的方法叫做 summaryStatistics() 。
     * 可以返回 IntSummaryStatistics、LongSummaryStatistics 或者 DoubleSummaryStatistic s，描述流中元素的各种摘要数据。
     * 在本例中，我们用这个方法来计算列表的最大值和最小值。它也有 getSum() 和 getAverage() 方法来
     * 获得列表的所有元素的总和及平均值。
     */
    private  static void test7(){
        //获取数字的个数、最小值、最大值、总和以及平均值
        List<Integer> primes = Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 23, 29);
        IntSummaryStatistics stats = primes.stream().limit(5).filter((x) -> x%2 != 0).mapToInt((x) -> x).summaryStatistics();
        System.out.println("Count in List : " + stats.getCount());
        System.out.println("Highest prime number in List : " + stats.getMax());
        System.out.println("Lowest prime number in List : " + stats.getMin());
        System.out.println("Sum of all prime numbers : " + stats.getSum());
        System.out.println("Average of all prime numbers : " + stats.getAverage());


    }
    private  static void test8(){
        System.out.println("使用 Java 7: ");

        // 计算空字符串
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
        System.out.println("列表: " +strings);
        long count = getCountEmptyStringUsingJava7(strings);

        System.out.println("空字符数量为: " + count);
        count = getCountLength3UsingJava7(strings);

        System.out.println("字符串长度为 3 的数量为: " + count);

        // 删除空字符串
        List<String> filtered = deleteEmptyStringsUsingJava7(strings);
        System.out.println("筛选后的列表: " + filtered);

        // 删除空字符串，并使用逗号把它们合并起来
        String mergedString = getMergedStringUsingJava7(strings,", ");
        System.out.println("合并字符串: " + mergedString);
        List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);

        // 获取列表元素平方数
        List<Integer> squaresList = getSquares(numbers);
        System.out.println("平方数列表: " + squaresList);
        List<Integer> integers = Arrays.asList(1,2,13,4,15,6,17,8,19);

        System.out.println("列表: " +integers);
        System.out.println("列表中最大的数 : " + getMax(integers));
        System.out.println("列表中最小的数 : " + getMin(integers));
        System.out.println("所有数之和 : " + getSum(integers));
        System.out.println("平均数 : " + getAverage(integers));
        System.out.println("随机数: ");

        // 输出10个随机数
        Random random = new Random();

        for(int i=0; i < 10; i++){
            System.out.println(random.nextInt());
        }

        System.out.println("使用 Java 8: ");
        System.out.println("列表: " +strings);

        count = strings.stream().filter(string->string.isEmpty()).count();
        System.out.println("空字符串数量为: " + count);

        count = strings.stream().filter(string -> string.length() == 3).count();
        System.out.println("字符串长度为 3 的数量为: " + count);

        filtered = strings.stream().filter(string ->!string.isEmpty()).collect(Collectors.toList());
        System.out.println("筛选后的列表: " + filtered);

        mergedString = strings.stream().filter(string ->!string.isEmpty()).collect(Collectors.joining(", "));
        System.out.println("合并字符串: " + mergedString);

        squaresList = numbers.stream().map( i ->i*i).distinct().collect(Collectors.toList());
        System.out.println("Squares List: " + squaresList);
        System.out.println("列表: " +integers);

        IntSummaryStatistics stats = integers.stream().mapToInt((x) ->x).summaryStatistics();

        System.out.println("列表中最大的数 : " + stats.getMax());
        System.out.println("列表中最小的数 : " + stats.getMin());
        System.out.println("所有数之和 : " + stats.getSum());
        System.out.println("平均数 : " + stats.getAverage());
        System.out.println("随机数: ");

        random.ints().limit(10).sorted().forEach(System.out::println);

        // 并行处理
        count = strings.parallelStream().filter(string -> string.isEmpty()).count();
        System.out.println("空字符串的数量为: " + count);
    }

    private static int getCountEmptyStringUsingJava7(List<String> strings){
        int count = 0;

        for(String string: strings){

            if(string.isEmpty()){
                count++;
            }
        }
        return count;
    }

    private static int getCountLength3UsingJava7(List<String> strings){
        int count = 0;

        for(String string: strings){

            if(string.length() == 3){
                count++;
            }
        }
        return count;
    }

    private static List<String> deleteEmptyStringsUsingJava7(List<String> strings){
        List<String> filteredList = new ArrayList<String>();

        for(String string: strings){

            if(!string.isEmpty()){
                filteredList.add(string);
            }
        }
        return filteredList;
    }

    private static String getMergedStringUsingJava7(List<String> strings, String separator){
        StringBuilder stringBuilder = new StringBuilder();

        for(String string: strings){

            if(!string.isEmpty()){
                stringBuilder.append(string);
                stringBuilder.append(separator);
            }
        }
        String mergedString = stringBuilder.toString();
        return mergedString.substring(0, mergedString.length()-2);
    }

    private static List<Integer> getSquares(List<Integer> numbers){
        List<Integer> squaresList = new ArrayList<Integer>();

        for(Integer number: numbers){
            Integer square = new Integer(number.intValue() * number.intValue());

            if(!squaresList.contains(square)){
                squaresList.add(square);
            }
        }
        return squaresList;
    }

    private static int getMax(List<Integer> numbers){
        int max = numbers.get(0);

        for(int i=1;i < numbers.size();i++){

            Integer number = numbers.get(i);

            if(number.intValue() > max){
                max = number.intValue();
            }
        }
        return max;
    }

    private static int getMin(List<Integer> numbers){
        int min = numbers.get(0);

        for(int i=1;i < numbers.size();i++){
            Integer number = numbers.get(i);

            if(number.intValue() < min){
                min = number.intValue();
            }
        }
        return min;
    }

    private static int getSum(List numbers){
        int sum = (int)(numbers.get(0));

        for(int i=1;i < numbers.size();i++){
            sum += (int)numbers.get(i);
        }
        return sum;
    }

    private static int getAverage(List<Integer> numbers){
        return getSum(numbers) / numbers.size();
    }




}
