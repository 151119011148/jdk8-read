package com.example.sourcecode.study.Self.jdk8;

/**
 函数式接口(Functional Interface)就是一个有且仅有一个抽象方法，但是可以有多个非抽象方法的接口。

 函数式接口可以被隐式转换为 lambda 表达式。

 Lambda 表达式和方法引用（实际上也可认为是Lambda表达式）上。
 */
public class FunctionInterfaceTest {

    @FunctionalInterface
    interface GreetingService {
        void sayMessage(String message);
    }

    public static void main(String[] args) {
        GreetingService greetService1 = message -> System.out.println("Hello " + message);
    }


}
