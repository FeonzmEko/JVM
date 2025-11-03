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

### 运行时常量池

* 常量池，就是一张表，虚拟机指令根据这张常量表找到要执行的类名，方法名，参数类型，字面量等信息
* 运行时常量池，常量池是*.class文件中的，当该类被加载，他的常量池信息就会放入运行时常量池，并把里面的符号地址变为真实地址

### StringTable串池

#### 特性

* 常量池中的字符串仅是符号，第一次用到时才变为对象
* 利用串池的机制，来避免重复创建字符串对象
* 字符串变量的拼接原理是`StringBuilder`
* 字符串常量拼接的原理是编译期优化
* 可以使用`intern`方法，主动将串池中还没有的字符串放入串池，如果有，则返回串池对象

```java
// StringTable ["a","b","ab"] hashtable结构，不可扩容
public class Demo2_1 {
    // 常量池中的信息，都会被加载到运行时常量池中，这时a b ab都是常量池中的符号，还没有变为java字符串对象
    // ldc #2 会把 a 符号变为 "a" 字符串对象
    // ldc #3 会把 b 符号变为 "b" 字符串对象
    // ldc #4 会把 ab 符号变为 "ab" 字符串对象
    public static void main(String[] args) {
        String s1 = "a"; // 懒惰的，延迟成为对象
        String s2 = "b";
        String s3 = "ab";
        String s4 = s1 + s2; // new StringBuilder().append("a").append("b").toString()  new String("ab")
        String s5 = "a" + "b"; // javac在编译期间的优化，结果已经在编译期间确定为ab

        System.out.println(s3 == s4);
        System.out.println(s3 == s5);
    }
}
```

#### 位置

![image-20251101203522793](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251101203522793.png)

#### 垃圾回收

在堆内存紧张时，才会触发垃圾回收机制来回收串池当中的字符串常量

#### 性能调优

* 调大`StringTableSize`
* 考虑将字符串对象是否入串池

#### 问题

1. 字符串常量拼接的原理是编译期优化是什么意思？

   答：当编译器遇到 **常量字符串拼接**（例如：`"a" + "b"`），它会在 **编译阶段** 就直接将这些字符串拼接起来，得到最终的结果。这样，程序在运行时就不需要执行字符串拼接操作，从而提高性能。例如`String s5 = "a" + "b";`，会把字符串a，b存入常量池之后，把ab也存入常量池

## 直接内存

![image-20251101211841397](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251101211841397.png)

### 定义

* 常见于NIO操作时，用于数据缓冲区
* 分配回收成本较高，但读写性能高
* 不受JVM内存回收管理

### 分配和回收原理

* 使用了`Unsafe`对象完成直接内存的分配回收，并且回收需要主动调用`freeMemory`方法
* `ByteBuffer`的实现类内部，使用了`Cleaner`（虚引用）来监测ByteBuffer对象，一旦ByteBuffer对象被垃圾回收，那么就会由`ReferenceHandler`线程通过Cleaner的`clean`方法调用`freeMemory`来释放直接内存



# 垃圾回收

堆空间

JDK1.7：新生代，老年代，永久代

JDK1.8：新生代，老年代，元空间

![](https://oss.javaguide.cn/github/javaguide/java/jvm/hotspot-heap-structure.png)



## 如何判断对象可以回收

### 引用计数法

对象被引用，则计数加一；不再被引用，则计数减一。

当引用计数变为0，则执行垃圾回收

**弊端：**存在循环引用问题，即互相引用对方

### 可达性分析算法

* Java虚拟机中的；阿吉回收器采用可达性分析来探索所有存活的对象
* 扫描堆中的对象，看是否能够沿着GC Root对象为起点的引用链找到该对象，找不到则表示可以回收
* 哪些对象可以作为GC Root？

**GC Roots** 是 Java 虚拟机（JVM）中用于垃圾回收的重要概念。它们是垃圾回收器判断对象是否存活的起点，通过可达性分析算法，GC Roots 作为根节点，沿着引用链查找可达的对象。未被引用链覆盖的对象会被视为垃圾并回收。

### 四种引用

1. 强引用
   * 只有所有GC Roots对象**都不通过【强引用】引用该对象**，该对象才能被垃圾回收
2. 软引用 SoftReference
   * 仅有软引用引用该对象时，在垃圾回收后，**内存仍不足时**会再次触发垃圾回收，回收软引用对象
   * 可以配合引用队列来释放软引用自身
3. 弱引用 WeakReference
   * 仅有弱引用引用该对象时，在垃圾回收时，**无论内存是否充足**，都会回收弱引用对象
   * 可以配合引用队列来释放弱引用自身
4. 虚引用 PhantomReference
   * 必须配合引用队列使用，主要配合**ByteBuffer**使用，被引用对象回收时，会将虚引用**入队**，由Reference Handler线程调用虚引用相关方法直接释放内存
5. 终结器引用 FinalReference
   * 无需手动编码，但其内部配合引用队列使用，在垃圾回收时，终结器引用入队（被引用对象暂时没有被回收），再有Finalizer线程通过终结器引用找到被引用对象并调用它的finalize方法，第二次GC时才能回收被引用对象



## 垃圾回收算法

### 标记清除

定义：Mark Sweep

* 速度较快
* 会造成内存碎片

![image-20251102113542020](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251102113542020.png)

### 标记整理

定义：Mark Compact

* 速度慢
* 没有内存碎片

![image-20251102114100240](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251102114100240.png)

### 复制

定义：Copy

* 不会有内存碎片
* 需要占用双倍内存空间

![image-20251102115131642](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251102115131642.png)

## 分代垃圾回收

![image-20251102121213449](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251102121213449.png)

* 对象首先分配在伊甸园区域
* 新生代空间不足时，触发minor gc，伊甸园和from存货的对象使用copy复制到to中，存活的对象年龄加1并且交换from to
* minor gc会引发stop the world，暂停其他用户的线程，等垃圾回收结束，用户线程才恢复运行
* 当对象寿命超过阈值时，会晋升至老年代，最大寿命是15（4bit）
* 当老年代空间不足，会先尝试触发minor gc，如果之后空间仍不足，那么触发full gc，STW的时间更长

### 相关VM参数

![image-20251102122112306](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251102122112306.png)

## 垃圾回收器

### 串行Serial

* 单线程
* 堆内存较小，适合个人电脑

![image-20251102132639221](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251102132639221.png)

### 吞吐量优先Parallel

* 多线程
* 堆内存较大，多核cpu
* 让单位时间内，STW的时间最短

![image-20251102133707598](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251102133707598.png)

### 响应时间优先CMS

* 多线程
* 堆内存较大，多核cpu
* 尽可能让单次STW的时间最短

### G1

定义：Garbage First

适用场景：

* 同时注重吞吐量和低延迟，默认的暂停目标是200ms
* 超大堆内存，会将堆划分为多个大小相等的region
* 整体上是标记+推理算法，两个区域之间是复制算法

## 垃圾回收调优

调油的本质是减少`STW`时间

### 调优领域

### 确定目标

### 最快的GC

* 查看full gc前后的内存占用，考虑以下来调优
  * 数据太多？
    * `select * from 大表`
  * 数据表示太臃肿？
    * 对象图
    * 对象大小？Integer 24 int 4
  * 是否存在内存泄漏？
    * static Map map
    * 软引用
    * 硬引用
    * 第三方缓存实现 redis

### 新生代调优

* 新生代特点
  * 所有的new操作的内存分配非常廉价
    * TLAB thread-local allocation buffer
  * 死亡对象的回收代价是0
  * 大部分对象用过即死
  * Minor GC 的时间远远低于 Full GC
* 幸存区大到能保留【当前活跃对象+需要晋升的对象】
* 晋升阈值配置得当，让长时间存活对象尽快晋升

### 老年代调优

以CMS为例

* CMS的老年代内存越大越好
* 先尝试不做调优，如果没有full gc那么已经...，否则先尝试优先调优新生代
* 观察发生full gc时老年代内存占用，将老年代内存预设调大
  * XX:CMSInitiatingOccupancyFraction=percent



# 类加载与字节码技术

## 类文件结构

## 字节码指令

### 入门

### javap工具

### 运行流程

* 原始java代码
* 编译后的字节码文件
  * `javap -v HelloWorld.class`
* 常量池载入运行时常量池
* 方法字节码载入方法区
* main线程开始运行，分配栈帧内存

### 方法调用

用对象调用类中静态方法会产生两条不必要的虚拟机字节码指令，所以只推荐用类来调用。

### 多态的原理

当执行`invokevirtual`指令时，

* 先通过栈帧中的对象引用找到对象
* 分析对象头，找到对象的实际class
* class结构中有vtable，它在类加载的链接阶段就已经根据方法的重写规则生成好了
* 查表得到方法的具体地址
* 执行方法的字节码指令

### 异常处理

#### 为什么finally代码块一定会被执行？

通过`javap -v Target.class`指令反编译成字节码后，可以看到finally代码块所对应的字节码被复制了三份，分别放入try，catch以及catch剩余的异常类型流程里，所以一定会被执行。

```java
public class Demo3_12_1 {
    public static void main(String[] args) {
        int result = test();
        System.out.println(result);
    }

    private static int test() {
        try {
            int i = 1 / 0;
            return 10;
        } finally {
            // finally内return会吞异常
            return 20;
        }
    }
}
```

* 由于finally中的ireturn被插入了所有可能的流程，因此返回结果肯定以finally为准
* 因此存在finally内return会吞异常的问题



## 语法糖



## 类加载阶段

### 加载

![image-20251103192517448](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251103192517448.png)

### 链接

* 准备：为static变量分配空间，设置默认值
  * static变量在JDK 7之前存储于instanceKlass末尾，从JDK 7开始，存储于_java_mirror末尾
  * static变量分配空间和赋值是两个步骤，分配空间在准备阶段完成，赋值在初始化阶段完成
  * 如果static变量是final的基本类型，那么编译阶段值就确定了，赋值在准备阶段完成
  * 如果static变量是final的，但属于引用类型，那么赋值也会在初始化阶段完成

### 解析

解析阶段是虚拟机将常量池内的符号引用替换为直接引用的过程，也就是得到类或者字段、方法在内存中的指针或者偏移量。

### 初始化

