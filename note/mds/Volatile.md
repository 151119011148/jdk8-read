Volatile

![Threads](../images/volatile可见性.png)

![Threads](../images/volatile有序性.png)

volatile为什么没有原子性?

![Threads](../images/volatile原子性.png)

　　明白了内存屏障（memory barrier）这个CPU指令，回到前面的JVM指令：从Load到store到内存屏障，一共4步，其中最后一步jvm让这个最新的变量的值在所有线程可见，也就是最后一步让所有的CPU内核都获得了最新的值，但中间的几步（从Load到Store）是不安全的，中间如果其他的CPU修改了值将会丢失。
