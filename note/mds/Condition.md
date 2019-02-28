
Condition
　　
　　我们知道任意一个Java对象，都拥有一组监视器方法（定义在java.lang.Object上），主要包
括wait()、notify()以及notifyAll()方法，这些方法与synchronized同步关键字配合，可以实现等待/通知模式

　　JUC包提供了Condition来对锁进行精准控制，Condition是一个多线程协调通信的工具类，可以让某些线程
一起等待某个条件（condition），只有满足条件时，线程才会被唤醒。

condition使用案例

ConditionWait
```java
public class ConditionDemoWait implements Runnable{
    private Lock lock;
    private Condition condition;
    public ConditionDemoWait(Lock lock, Condition condition){
        this.lock=lock;
        this.condition=condition;
    }
    @Override
    public void run() {
        System.out.println("begin -ConditionDemoWait");
        try {
            lock.lock();
            condition.await();
            System.out.println("end - ConditionDemoWait");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
```

ConditionSignal
```java
public class ConditionDemoSignal implements Runnable{
    private Lock lock;
    private Condition condition;
    public ConditionDemoSignal(Lock lock, Condition condition){
        this.lock=lock;
        this.condition=condition;
    }
    @Override
    public void run() {
        System.out.println("begin -ConditionDemoSignal");
        try {
            lock.lock();
            condition.signal();
            System.out.println("end - ConditionDemoSignal");
        }finally {
            lock.unlock();
        }
    }
}
```

　　通过这个案例简单实现了wait和notify的功能，当调用await方法后，当前线程会释放锁并等待，而其他线程调用
condition对象的signal或者signalall方法通知并被阻塞的线程，然后自己执行unlock释放锁，被唤醒的线程获得之
前的锁继续执行，最后释放锁。

　　所以，condition中两个最重要的方法，一个是await，一个是signal方法

    await:把当前线程阻塞挂起
    signal:唤醒阻塞的线程
    
await方法

　　调用Condition的await()方法（或者以await开头的方法），会使当前线程进入等待队列并释放锁，同时线程状态变
为等待状态。当从await()方法返回时，当前线程一定获取了Condition相关联的锁

```java
public final void await() throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    Node node = addConditionWaiter(); //创建一个新的节点，节点状态为condition，采用的数据结构仍然是链表
    int savedState = fullyRelease(node); //释放当前的锁，得到锁的状态，并唤醒AQS队列中的一个线程
    int interruptMode = 0;
    //如果当前节点没有在同步队列上，即还没有被signal，则将当前线程阻塞
    //isOnSyncQueue 判断当前 node 状态,如果是 CONDITION 状态,或者不在队列上了,就继续阻塞,还在队列上且不是 CONDITION 状态了,就结束循环和阻塞
    while (!isOnSyncQueue(node)) {//第一次判断的是false，因为前面已经释放锁了
        LockSupport.park(this); // 第一次总是 park 自己,开始阻塞等待
        // 线程判断自己在等待过程中是否被中断了,如果没有中断,则再次循环,会在 isOnSyncQueue 中判断自己是否在队列上.
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    // 当这个线程醒来,会尝试拿锁, 当 acquireQueued 返回 false 就是拿到锁了.
    // interruptMode != THROW_IE -> 表示这个线程没有成功将 node 入队,但 signal 执行了 enq 方法让其入队了.
    // 将这个变量设置成 REINTERRUPT.
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    // 如果 node 的下一个等待者不是 null, 则进行清理,清理 Condition 队列上的节点
    // 如果是 null ,就没有什么好清理的了.
    if (node.nextWaiter != null) // clean up if cancelled
        unlinkCancelledWaiters();
    // 如果线程被中断了,需要抛出异常.或者什么都不做
    if (interruptMode != 0)
        reportInterruptAfterWait(interruptMode);
}
```

signal

　　调用Condition的signal()方法，将会唤醒在等待队列中等待时间最长的节点（首节点），在唤醒节点之前，会将节
点移到同步队列中

```java
public final void signal() {
    if (!isHeldExclusively()) //先判断当前线程是否获得了锁
        throw new IllegalMonitorStateException();
    Node first = firstWaiter; // 拿到 Condition 队列上第一个节点
    if (first != null)
        doSignal(first);
}
private void doSignal(Node first) {
    do {
        if ( (firstWaiter = first.nextWaiter) == null)// 如果第一个节点的下一个节点是 null,那么, 最后一个节点也是 null.
            lastWaiter = null; // 将 next 节点设置成 null
            first.nextWaiter = null;
        } while (!transferForSignal(first) && (first = firstWaiter) != null);
}
```
　　该方法先是 CAS 修改了节点状态，如果成功，就将这个节点放到 AQS 队列中，然后唤醒这个节点上的线程。此
时，那个节点就会在 await 方法中苏醒
```java
 final boolean transferForSignal(Node node) {
    if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
        return false;
    Node p = enq(node);
    int ws = p.waitStatus;
    // 如果上一个节点的状态被取消了, 或者尝试设置上一个节点的状态为 SIGNAL 失败了(SIGNAL 表示: 他的next 节点需要停止阻塞),
    if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
        LockSupport.unpark(node.thread); // 唤醒输入节点上的线程.
    return true;
}
```

