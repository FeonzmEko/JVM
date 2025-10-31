# 什么是JVM

定义：Java Virtual Machine -java程序的运行环境（java二进制字节码的运行环境）

好处：

* 一次编写，到处运行
* 自动内存管理，垃圾回收功能
* 数组下标越界检查
* 多态

比较：

jvm，jre，jdk

![image-20251031162217282](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251031162217282.png)

# 学JVM用处

* 面试
* 理解底层的实现原理
* 中高级程序员的必备技能

![image-20251031163620020](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251031163620020.png)



# 内存结构

## 程序计数器

**作用：**

记住下一条jvm指令的执行地址

![image-20251031165122156](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251031165122156.png)

**特点：**

* 线程私有
* 不存在内存溢出

## 虚拟机栈

栈：线程运行需要的内存空间

栈帧：每个方法运行时需要的内存

每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法

![image-20251031175009595](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251031175009595.png)

活动栈帧位于栈顶。



### 问题解析

1. 垃圾回收时是否涉及栈内存？
   * JVM 的垃圾回收主要针对堆内存中的对象，而不涉及栈内存
   * 每当方法被调用，会为该方法分配一个栈帧，方法调用结束时销毁
2. 栈内存分配越大越好吗？
   * 不会，占内存分配越大，可用的线程数越小

3. 方法内的局部变量是否线程安全？

   * 如果方法内局部变量没有逃离方法的作用范围，则线程安全
   * 如果是局部变量引用了对象，并讨论里方法的作用范围，考虑线程安全（不一定安全）

   

### 栈内存溢出

`StackOverflowError`

* 栈帧过多导致栈内存溢出
* 栈帧过大导致占内存溢出

### 线程运行诊断

jstack工具

* CPU占用过多
* 程序运行很长时间没有结果


## 本地方法栈

本地方法栈（Native Method Stack）与虚拟机栈所发挥的作用是非常相似的，其区别是虚拟机栈为虚拟机执行Java方法(也就是**字节码**)服务，而本地方法栈则是为虚拟机使用到的本地（Native）方法服务。

本地方法是C/C++语言实现的方法

## 堆

**堆**：通过`new`关键字创建对象都会使用堆内存

**特点：**

* 他是线程共享的，堆中对象都需要考虑线程安全的问题
* 有垃圾回收机制

### 堆内存溢出

`OutOfMemoryError`

### 堆内存诊断

1. jps工具
   * 查看当前系统中有哪些java进程
2. jmap工具
   * 查看堆内存占用情况
3. jconsole工具
   * 图形界面的多功能监测工具，可以连续监测



## 方法区

### 定义

在JVM内存结构中，**方法区（Method Area）** 是一块用于存放**类信息、常量、静态变量以及即时编译器编译后的代码**的内存区域。它是JVM堆外的一部分，也可以理解为“元空间”（Metaspace），尤其是在Java 8及之后版本中，方法区的实现已经被元空间取代，但其原理和功能大致相同

### 方法区内存溢出

* 1.8以前会导致永久代内存溢出
  * java.lang.OutOfMerroyError：PermGen space
* 1.8之后会导致元空间内存溢出
  * java.lang.OutOfMerroyError：Metaspace