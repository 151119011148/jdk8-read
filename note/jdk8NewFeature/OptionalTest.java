package com.example.sourcecode.study.Self.jdk8;

import org.junit.Test;

import java.util.Optional;

/**
 * Create by  GF  in  10:52 2019/1/2
 * Description:
 * Modified  By:
 */
public class OptionalTest {

    @Test
    public static void testOptional1() {
        Integer value1 = null;
        Integer value2 = new Integer(10);

        // Optional.ofNullable - 允许传递为 null 参数
        Optional<Integer> a = Optional.ofNullable(value1);

        // Optional.of - 如果传递的参数是 null，抛出异常 NullPointerException
        Optional<Integer> b = Optional.of(value2);

        System.out.println("第一个参数值存在: " + a.isPresent());
        System.out.println("第二个参数值存在: " + b.isPresent());

        // Optional.orElse - 如果值存在，返回它，否则返回默认值
        Integer value3 = a.orElse(new Integer(0));

        //Optional.get - 获取值，值需要存在
        Integer value4 = b.get();
        System.out.println(value3 + value4);
    }


    @Test
    public void testOptional2() {
        /*of方法 为非null的值创建一个Optional*/
        //of方法通过工厂方法创建Optional类。
        //需要注意的是，创建对象时传入的参数不能为null。
        //如果传入参数为null，则抛出NullPointerException 。
        Optional<String> op1 = Optional.of("hello");
 
        /*ofNullable方法 为指定的值创建一个Optional，如果指定的值为null，则返回一个空的Optional。*/
        //ofNullable与of方法相似，唯一的区别是可以接受参数为null的情况
        Optional<String> op2 = Optional.ofNullable(null);
 
         /*isPresent方法 如果值存在返回true，否则返回false。*/
         /*get方法 如果Optional有值则将其返回，否则抛出NoSuchElementException。*/
        if (op1.isPresent()) {
            System.out.println(op1.get());
        }
        if (op2.isPresent()) {
            System.out.println(op2.get());
        }
 
        /*ifPresent方法 如果Optional实例有值则为其调用consumer，否则不做处理*/
        //consumer接口中的方法只有参数没有返回值
        op1.ifPresent(str -> System.out.println(str));
        op2.ifPresent(str -> System.out.println(str));//这个不执行 因为op2里面的值是null
 
 
         /*orElse方法 如果有值则将其返回，否则返回指定的其它值。*/
        System.out.println(op1.orElse("如果op1中的值为null则返回这句话,否则返回这个值"));
        System.out.println(op2.orElse("如果op2中的值为null则返回这句话,否则返回这个值"));
 
 
        /*orElseGet方法 orElseGet与orElse方法类似，区别在于得到的默认值。orElse方法将传入的字符串作为默认值，orElseGet方法可以接受Supplier接口的实现用来生成默认值。*/
        //Supplier接口中的方法没有参数但是有返回值
        System.out.println(op1.orElseGet(() -> "自己定义的返回值"));
        System.out.println(op2.orElseGet(() -> "自己定义的返回值"));
 
 
        /*orElseThrow方法 如果有值则将其返回，否则抛出supplier接口创建的异常。*/
        //在orElseThrow中我们可以传入一个lambda表达式或方法，如果值不存在来抛出异常。
        //orElseThrow方法的声明如下 所有只能返回一个Throwable类型对象
        //public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X
        try {
            System.out.println(op1.orElseThrow(Exception::new));
            ;
            //System.out.println(op2.orElseThrow(Exception::new));;这个会抛出异常
        } catch (Exception e) {
            e.printStackTrace();
        }
 
 
 /*map方法 如果有值，则对其执行调用mapper函数得到返回值。*/
        //返回值并且依然Optional包裹起来,其泛型和你返回值的类型一致
        //public<U> Optional<U> map(Function<? super T, ? extends U> mapper)
        Optional<Integer> map1 = op1.map(str -> 1);
        System.out.println(map1.get());
        Optional<Double> map2 = op2.map(str -> 1.2);
        System.out.println(map2.orElse(0.0));
 
 
 /*flatMap方法 如果有值，为其执行mapper函数返回Optional类型返回值，否则返回空Optional。*/
        //flatMap与map方法类似，区别在于flatMap中的mapper返回值必须是Optional。调用结束时，flatMap不会对结果用Optional封装。
        //需要我们自己把返回值封装为Optional
        //public<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper)
        System.out.println(op1.flatMap(str -> Optional.of(str + "_briup")).get());
        //op1.flatMap(str->"");编译出错
 
 
 /*filter方法 如果有值并且满足断言条件返回包含该值的Optional，否则返回空Optional。*/
        //public Optional<T> filter(Predicate<? super T> predicate)
        op1 = op1.filter(str -> str.length() < 10);
        System.out.println(op1.orElse("值为null"));
        op1 = op1.filter(str -> str.length() > 10);
        System.out.println(op1.orElse("值为null"));

    }



}
