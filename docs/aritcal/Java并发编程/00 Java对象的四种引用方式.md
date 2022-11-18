# Java对象的四种引用方式
# 强引用
```java
Object obj = new Object();
```
上述Object这类对象就具有强引用，属于不可回收的资源，垃圾回收器绝不会回收它。当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，也不会靠回收具有强引用的对象，来解决内存不足的问题。

1. 首先看等号的右侧。new是在内存中为对象开辟空间。具体来说，new是在内存的堆(heap)上为对象开辟空间。这一空间中，保存有对象的数据和方法。
2. 再看等号的左侧。obj指代一个Object对象，被称为对象引用(reference)。实际上，obj并不是对象本身，而是类似于一个指向对象的指针。obj存在于内存的**栈(stack)**中。
3. 当我们用等号赋值时，是将右侧new在堆中创建对象的地址赋予给对象引用。


### 垃圾回收
随着方法调用的结束，引用和基本类型变量会被清空。由于对象存活于堆，所以对象所占据的内存不会随着方法调用的结束而清空。进程空间可能很快被不断创建的对象占满。Java内建有垃圾回收(garbage collection)机制，用于清空不再使用的对象，以回收内存空间。
垃圾回收的基本原则是，当存在引用指向某个对象时，那么该对象不会被回收; 当没有任何引用指向某个对象时，该对象被清空。它所占据的空间被回收。

值得注意的是：如果想中断或者回收强引用对象，可以显式地将引用赋值为null，这样的话JVM就会在合适的时间，进行垃圾回收。

# 软引用
如果一个对象只具有软引用，那么它的性质属于可有可无的那种。如果此时内存空间足够，垃圾回收器就不会回收它，如果内存空间不足了，就会回收这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被程序使用，如图片缓存框架中缓存图片就是通过软引用实现。软引用可用来实现内存敏感的告诉缓存。软引用可以和一个引用队列联合使用，如果软件用所引用的对象被垃圾回收，Java虚拟机就会把这个软引用加入到与之关联的引用队列中。
```java
Object obj = new Object();
ReferenceQueue queue = new ReferenceQueue();
SoftReference reference = new SoftReference(obj, queue);
//强引用对象置空，保留软引用
obj = null;
```
当内存不足时，软引用对象被回收时，reference.get()为null，`此时软引用对象的作用已经发挥完毕，这时将其添加进ReferenceQueue 队列中`

如果要判断哪些软引用对象已经被清理：
```java
SoftReference ref = null;
while((ref = (SoftReference) queue.poll()) != null) {
    //清除软引用对象
}
```
# 弱引用
如果一个对象具有弱引用，那其的性质也是可有可无的状态。

而弱引用和软引用的区别在于：弱引用的对象拥有更短的生命周期，只要垃圾回收器扫描到它，不管内存空间充足与否，都会回收它的内存。

同样的弱引用也可以和引用队列一起使用。
```java
Object o2 = new Object();//这样定义的默认就是强引用
ReferenceQueue<Object> referenceQueue2 = new ReferenceQueue<>();
WeakReference<Object> weakReference = new WeakReference<>(o2, referenceQueue2);
System.out.println(o2);
System.out.println(weakReference.get());
System.out.println(referenceQueue2.poll());
System.out.println("---------");
o2 = null;
System.gc();
TimeUnit.SECONDS.sleep(5);
System.out.println(o2);
System.out.println(weakReference.get());
System.out.println(referenceQueue2.poll());
```

应用场景：弱引用适用于内存敏感的缓存，如ThreadLocal中的key就用到了弱引用。
# 虚引用

虚引用和前面的软引用、弱引用不同，它并不影响对象的生命周期。如果一个对象与虚引用关联，则跟没有引用与之关联一样，在任何时候都可能被垃圾回收器回收。

注意：虚引用必须和引用队列关联使用，当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会把这个虚引用加入到与之关联的引用队列中。程序可以通过判断引用队列中是否已经加入了虚引用，来了解被引用的对象是否将要被垃圾回收。如果程序发现某个虚引用已经被加入到引用队列，那么就可以在所引用的对象的内存被回收之前采取必要的行动。

```java
Object o1 = new Object();//这样定义的默认就是强引用
ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
PhantomReference<Object> phantomReference = new PhantomReference<>(o1, referenceQueue);
System.out.println(o1);
System.out.println(phantomReference.get());
System.out.println(referenceQueue.poll());

o1 = null;
System.gc();
TimeUnit.SECONDS.sleep(5);
System.out.println(o1);
System.out.println(phantomReference.get());
System.out.println(referenceQueue.poll());
```