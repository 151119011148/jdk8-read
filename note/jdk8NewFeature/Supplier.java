package com.example.sourcecode.study.Self.jdk8;

import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface Supplier<T> {
    T get();
}

class Car {


    @Override
    public String toString() {
        return "Car{}";
    }

    public static void main(String[] args) {
        //构造器引用：它的语法是Class::new，或者更一般的Class< T >::new实例如下：
        final com.example.sourcecode.study.Self.jdk8.Car car = com.example.sourcecode.study.Self.jdk8.Car.create(com.example.sourcecode.study.Self.jdk8.Car:: new);
        final List<com.example.sourcecode.study.Self.jdk8.Car> cars = Arrays.asList( car );

        //静态方法引用：它的语法是Class::static_method，实例如下：
        cars.forEach(com.example.sourcecode.study.Self.jdk8.Car:: collide);

        //特定类的任意对象的方法引用：它的语法是Class::method实例如下：
        cars.forEach(com.example.sourcecode.study.Self.jdk8.Car:: repair);

        //特定对象的方法引用：它的语法是instance::method实例如下：
        final com.example.sourcecode.study.Self.jdk8.Car police = com.example.sourcecode.study.Self.jdk8.Car.create(com.example.sourcecode.study.Self.jdk8.Car:: new);
        cars.forEach(police :: follow);

    }

    //Supplier是jdk1.8的接口，这里和lambda一起使用了
    public static com.example.sourcecode.study.Self.jdk8.Car create(final com.example.sourcecode.study.Self.jdk8.Supplier<com.example.sourcecode.study.Self.jdk8.Car> supplier) {
        return supplier.get();
    }

    public static void collide(final com.example.sourcecode.study.Self.jdk8.Car car) {
        System.out.println("Collided " + car.toString());
    }

    public void follow(final com.example.sourcecode.study.Self.jdk8.Car another) {
        System.out.println("Following the " + another.toString());
    }

    public void repair() {
        System.out.println("Repaired " + this.toString());
    }
}