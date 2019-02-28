package com.example.sourcecode.study.Self.jdk8;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 函数式接口(Functional Interface)就是一个有且仅有一个抽象方法，但是可以有多个非抽象方法的接口。
 * <p>
 * 函数式接口可以被隐式转换为 lambda 表达式。
 * <p>
 * Lambda 表达式和方法引用（实际上也可认为是Lambda表达式）上。
 */
public class FunctionInterfaceTest {

    @FunctionalInterface
    interface GreetingService {
        void sayMessage(String message);
    }

    @Test
    public  void testFunctionalInterface() {
        GreetingService greetService1 = message -> System.out.println("Hello " + message);
    }

//-------------------------------------------------------------------------------------------------------------------------------
//Java 8 允许你使用 :: 关键字来传递方法(静态方法和非静态方法)
    @FunctionalInterface
    interface Action3 {
        String run(int Integer);
    }


    @Test
    public  void testDouble() {
        com.example.sourcecode.study.Self.jdk8.FunctionInterfaceTest t = new com.example.sourcecode.study.Self.jdk8.FunctionInterfaceTest();
        //使用Lambda引用类的静态方法
        //能引用Integer类中的静态方法toBinaryString的原因是:
        //Action3接口中只有一个方法且方法的参数类型和返回值类型
        //与Integer类中的静态方法toBinaryString的参数类型、返回类型是一致的
        Action3 a3 = Integer::toBinaryString;
        a3 = String::valueOf;
        System.out.println(a3.run(4));

        //使用Lambda引用对象的非静态方法
        //能引用对象t中的非静态方法test的原因是和上面的描述是一致的
        Action3 aa3 = t::Double;
        aa3 = com.example.sourcecode.study.Self.jdk8.FunctionInterfaceTest::Double2;
        System.out.println(aa3.run(4));
    }

    //-------------------------------------------------------------------------------------------------------------------------------
    //下面是一个接口中带泛型的时候特殊例子: 可以使用  类名::非静态方法  的形式引用方法
    public String Double(int i) {
        return "i=" + i;
    }

    public static String Double2(int i) {
        return "i=" + i;
    }


    interface Action<T> {
         void run(T t);
    }

     class Model {

        public void test1() {
            System.out.println("test1");
        }

        public void test2(Model a) {
            System.out.println("test2");
        }

        public int test3() {
            System.out.println("test3");
            return 1;
        }

    }

    @Test
    public  void testDouble2() {
        Model m = new Model();
        //方法有一个参数，然后没返回类型,这里参数类型会自动识别
        Action<Model> a1 = (s) -> System.out.println("hello");
        a1.run(m);

        //注意:如果这里泛型类型不是Model 那么就不能引用Model中的方法
        //可以引用Model类中任意方法 只要满足一点:该方法没有参数
        //将来run方法中就会调用Model类型对象m的此处引用的方法
        Action<Model> a2 = Model:: test3;
        Action<Model> modelAction = Model:: test1;
        a2.run(m);
        modelAction.run(m);

        //引用对象m中的test2方法
        //因为test2方法的参数和返回类型和Action接口的方法完全一致
        Action<Model> a3 = m :: test2;
        a3.run(m);
    }

    //-------------------------------------------------------------------------------------------------------------------------------
    // Java 8 允许你使用 :: 关键字来引用构造函数
    class Action4 {
        private String name;

        public Action4() {
        }

        public Action4(String name) {
            this.name = name;
        }

        public void say() {
            System.out.println("name = " + name);
        }
    }

    interface Action4Creator {
        Action4 create(String name);
    }

    @Test
    public  void testDouble3() {
        //Lambda表达式引用构造函数
        //根据构造器的参数来自动匹配使用哪一个构造器
        Action4Creator creator = Action4::new;
        Action4 a4 = creator.create("zhangsan");
        a4.say();
    }

//-------------------------------------------------------------------------------------------------------------------------------
/* lambda表达式中的变量访问*/
    private static int j;
    private int k;

    interface Action5 {
        void run(int i);
    }

    @Test
    public void testVariable() {
        //局部变量不可再改变 否则编译不通过
        int num = 10;
        //成员变量可再改变
        j = 20;
        k = 30;

        //lambda表达式中可以访问成员变量也可以方法局部变量
        Action5 a5 = (i) -> System.out.println("操作后:i=" + (i + num + j + k));
        a5.run(1);

        //但是这个被访问的变量默认变为final修饰的 不可再改变 否则编译不通过
        //num = 60;
        j = 50;
        k = 70;

    }

//-------------------------------------------------------------------------------------------------------------------------------
/**
 * Predicate接口和lambda表达式
 * java.util.function.Predicate接口是用来支持java函数式编程新增的一个接口,
 * 使用这个接口和lamb表达式就可以以更少的代码为API方法添加更多的动态行为。
 */

@Test
public void testPredicate() {

    List<String> languages = Arrays.asList("Java", "html5", "JavaScript", "C++", "hibernate", "PHP");

//开头是J的语言
    filter(languages, (name) -> name.startsWith("J"));
//5结尾的
    filter(languages, (name) -> name.endsWith("5"));
//所有的语言
    filter(languages, (name) -> true);
//一个都不显示
    filter(languages, (name) -> false);
//显示名字长度大于4
    filter(languages, (name) -> name.length() > 4);
    System.out.println("-----------------------");
//名字以J开头并且长度大于4的
    Predicate<String> c1 = (name) -> name.startsWith("J");
    Predicate<String> c2 = (name) -> name.length() > 4;
    filter(languages, c1.and(c2));
//名字不是以J开头
    Predicate<String> c3 = (name) -> name.startsWith("J");
    filter(languages, c3.negate());
//名字以J开头或者长度小于4的
    Predicate<String> c4 = (name) -> name.startsWith("J");
    Predicate<String> c5 = (name) -> name.length() < 4;
    filter(languages, c4.or(c5));
//名字为Java的
    filter(languages, Predicate.isEqual("Java"));
//判断俩个字符串是否相等
    boolean test = Predicate.isEqual("hello").test("world");
    System.out.println(test);
}

    public static void filter(List<String> languages, Predicate<String> condition) {
        for (String name : languages) {
            if (condition.test(name)) {
                System.out.println(name + " ");
            }
        }
    }

//-------------------------------------------------------------------------------------------------------------------------------
    /**
     * Function有一个参数并且返回一个结果，并附带了一些可以和其他函数组合的默认方法
     * compose方法表示在某个方法之前执行
     * andThen方法表示在某个方法之后执行
     *
     * 注意：compose和andThen方法调用之后都会把对象自己本身返回，这可以方便链式编程
     *
     default <V> Function<T,V> andThen(Function<? super R,? extends V> after) 返回一个先执行当前函数对象apply方法
     再执行after函数对象apply方法的函数对象。

      default <V> Function<T,V> compose(Function<? super V,? extends T> before)返回一个先执行before函数对象apply方法
     再执行当前函数对象apply方法的函数对象。

      static <T> Function<T,T> identity() 返回一个执行了apply()方法之后只会返回输入参数的函数对象
     */

    public interface Function<T, R> {

        R apply(T t);

        default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
            Objects.requireNonNull(before);
            return (V v) -> apply(before.apply(v));
        }

        default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (T t) -> after.apply(apply(t));
        }

        //注意: t->t是(t)->t的简写
        //t->t是作为方法identity的返回值的,也就是Function类型对象
        //类似于这样的写法:Function<Object, Object> f = t->t;
        //那么f.apply("test") 返回字符串"test"
        //传入什么则返回什么
        static <T> Function<T, T> identity() {
            return t -> t;
        }
    }

    @Data
    @AllArgsConstructor
    private static class Student{
        private String name;
    }

    @Test
    public void testFunction() {
        /*用户注册输入一个名字tom*/
        String name = "tom";

        /*使用用户的输入的名字创建一个对象*/
        Function<String, Student> function = (s) -> new Student(s);
        //注意上面的代码也可以写出这样，引用类中的构造器
        //Function<String, Student> function =Student::new;
        Student student1 = function.apply(name);
        System.out.println(student1.getName());

        /*需求改变,使用name创建Student对象之前需要给name加一个前缀*/
        Function<String, String> before = (s) -> "before_" + s;
        //表示function调用之前先执行before对象的方法,把before对象的方法返回结果作为function对象方法的参数
        Student student2 = function.compose(before).apply(name);
        System.out.println(student2.getName());

        /*获得创建好的对象中的名字的长度*/
        Function<Student, Integer> after = (stu) -> stu.getName().length();
        //before先调用方法,结果作为参数传给f1来调用方法,结果再作为参数传给after,结果就是我们接收的数据
        int len = function.compose(before).andThen(after).apply(name);
        System.out.println(len);
    }






}

