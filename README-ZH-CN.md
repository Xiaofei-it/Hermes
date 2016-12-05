# Hermes
一套新颖巧妙易用的Android进程间通信IPC框架。

Hermes是一套新颖巧妙易用的Android进程间通信IPC框架。这个框架使得你不用了解IPC机制就可以进行进程间通信，像调用本地函数一样调用其他进程的函数。

Hermes的demo请点击[https://github.com/Xiaofei-it/Hermes-IPC-Demo](https://github.com/Xiaofei-it/Hermes-IPC-Demo)

另外，[HermesEventBus](https://github.com/eleme/HermesEventBus)是基于Hermes和EventBus开发的进程间传递事件的库。

可能读者会觉得Hermes还是有点难用。我已经完成了基本功能并且做了许多性能优化。我下一步会简化使用步骤，使Hermes变得更简单可用，到时候发布0.7.0版。

##特色

1. 使得进程间通信像调用本地函数一样方便简单。

2. 轻而易举在本地进程创建其他进程类的对象，轻而易举在本进程获取其他进程的单例，轻而易举在本进程使用其他进程的工具类。

3. 支持进程间函数回调，调用其他进程函数的时候可以传入回调函数，让其他进程回调本进程的方法。

4. 自带内存优化。Hermes内置两个垃圾回收器，本地进程在远端进程创建的实例和本地进程传给远端进程的回调接口会被自动回收。（为什么用中文说得这么啰嗦？？？）


##基本原理

IPC的主要目的是调用其他进程的函数，Hermes让你方便地调用其他进程函数，调用语句和本地进程函数调用一模一样。

比如，单例模式经常在Android App中使用。假设有一个app有两个进程，它们共享如下单例：

```
@ClassId(“Singleton”)
public class Singleton {

    private static Singleton sInstance = null;

    private volatile String mData;

    private Singleton() {
        mData = new String();
    }

    public static synchronized Singleton getInstance() {
        if (sInstance == null) {
            sInstance = new Singleton();
        }
        return sInstance;
    }

    @MethodId(“setData”)
    public void setData(String data) {
        mData = data;
    }

    @MethodId(“getData”)
    public String getData() {
        return mData;
    }

}
```
如果不使用Hermes，单例是无法共享的。

假设单例在进程A中，进程B想访问这个单例。那么你写如下接口：

```
@ClassId(“Singleton”)
public interface ISingleton {

    @MethodId(“setData”)
    void setData(String data);

    @MethodId(“getData”)
    String getData();

}
```

进程B使用单例的时候，代码如下：

```
//obtain the instance of Singleton
ISingleton singleton = Hermes.getInstance(ISingleton.class);

//Set a data
singleton.setData(“Hello, Hermes!”);

//Get the data
Log.v(TAG, singleton.getData());
```

是不是很神奇？

只要给Hermes.getInstance()传入这样的接口，Hermes.getInstance()便会返回和进程A中实例一模一样的实例。之后你在进程B中调用这个实例的方法时，进程A的同一个实例的方法也被调用。

但是，怎么写这种接口呢？很简单。比如，进程A有一个类Foo，你想在进程B中访问使用这个类。那么你写如下接口IFoo，加入同样的方法，再在类Foo和接口IFoo上加上同样的@ClassId注解，相同的方法上加上同样的@MethodId注解。之后你就可以在进程B使用Hermes.getInstance(IFoo.class)获取进程A的Foo实例。

##Gradle

```
dependencies {
    compile 'xiaofei.library:hermes:0.7.0'
}
```

##Maven

```
<dependency>
  <groupId>xiaofei.library</groupId>
  <artifactId>hermes</artifactId>
  <version>0.7.0</version>
  <type>pom</type>
</dependency>
```

##使用方法

接下来的部分将告诉你如何在其他进程调用主进程的函数。Hermes支持任意进程之间的函数调用，想要知道如何调用非主进程的函数，请看[这里](https://github.com/Xiaofei-it/Hermes/blob/master/AdvancedTutorial.md)。

###AndroidManifest.xml

在AndroidManifest.xml中加入如下声明，你可以加上其他属性。

```
<service android:name="xiaofei.library.hermes.HermesService$HermesService0">
</service>
```

###主进程初始化Hermes

在给其他进程提供函数的进程中，使用Hermes.init(Context)初始化。

###子进程连接Hermes

经常地，一个app有一个主进程。给这个主进程命名为进程A。

假设有一个进程B，想要调用进程A的函数。那么进程B应该连接Hermes。连接后才可以使用Hermes的服务。

你可以在进程B的Application.OnCreate()或者Activity.OnCreate()中对Hermes初始化。相应的API是Hermes.connect(Context)。

```
Hermes.connect(getApplicationContext());
```

你可以调用Hermes.isConnected()来查看通信的进程是否还活着。

###注册

进程A中，被进程B调用的类需要事先注册。有两种注册类的API：Hermes.register(Class<?>)和Hermes.register(Object)。Hermes.register(object)等价于Hermes.register(object.getClass())。

但是如果类上面没有加上注解，那么注册就不是必须的，Hermes会通过类名进行反射查找相应的类。详见“注意事项”的第3点。

###创建实例

进程B中，创建进程A中的实例有三种方法：Hermes.newInstance()、Hermes.getInstance()和Hermes.getUtilityClass()。

1. Hermes.newInstance(Class<T>, Object...)

   这个函数在进程A中创建指定类的实例，并将引用返回给进程B。函数的第二个参数将传给指定类的对应的构造器。
   ```
   @ClassId(“LoadingTask”)
   public class LoadingTask {

       public LoadingTask(String path, boolean showImmediately) {
           //...
       }

       @MethodId(“start”)
       public void start() {
           //...
       }
   }

   @ClassId(“LoadingTask”)
   public class ILoadingTask {
       @MethodId(“start”)
       void start();
   }
   ```
   在进程B中，调用Hermes.newInstance(ILoadingTask.class, “files/image.png”, true)便得到了LoadingTask的实例。

2. Hermes.getInstance(Class<T>, Object...)

   这个函数在进程A中通过指定类的getInstance方法创建实例，并将引用返回给进程B。第二个参数将传给对应的getInstance方法。

   这个函数特别适合获取单例，这样进程A和进程B就使用同一个单例。

   ```
   @ClassId(“BitmapWrapper”)
   public class BitmapWrapper {

       @GetInstance
       public static BitmapWrapper getInstance(String path) {
           //...
       }

       @GetInstance
       public static BitmapWrapper getInstance(int label) {
           //...
       }

       @MethodId(“show”)
       public void show() {
           //...
       }

   }

   @ClassId(“BitmapWrapper”)
   public class IBitmapWrapper {

       @MethodId(“show”)
       void show();
   
   }
   ```
   进程B中，调用Hermes.getInstance(IBitmapWrapper.class, “files/image.png”)或Hermes.getInstance(IBitmapWrapper.class, 1001)将得到BitmapWrapper的实例。

3. Hermes.getUtilityClass(Class<T>)

   这个函数获取进程A的工具类。

   这种做法在插件开发中很有用。插件开发的时候，通常主app和插件app存在不同的进程中。为了维护方便，应该使用统一的工具类。这时插件app可以通过这个方法获取主app的工具类。
   ```
   @ClassId(“Maths”)
   public class Maths {

       @MethodId(“plus”)
       public static int plus(int a, int b) {
          //...
       }

       @MethodId(“minus”)
       public static int minus(int a, int b) {
           //...
       }

   }


   @ClassId(“Maths”)
   public class IMaths {

       @MethodId(“plus”)
       int plus(int a, int b);

       @MethodId(“minus”)
       int minus(int a, int b);
   }
   ```
   进程B中，使用下面代码使用进程A的工具类。
   ```
   IMaths maths = Hermes.getUtilityClass(IMaths.class);
   int sum = maths.plus(3, 5);
   int diff = maths.minus(3, 5);
   ```

##注意事项

1. 事实上，如果两个进程属于两个不同的app（分别叫App A和App B），App A想访问App B的一个类，并且App A的接口和App B的对应类有相同的包名和类名，那么就没有必要在类和接口上加@ClassId注解。但是要注意使用ProGuard后类名和包名仍要保持一致。

2. 如果接口和类里面对应的方法的名字相同，那么也没有必要在方法上加上@MethodId注解，同样注意ProGuard的使用后接口内的方法名字必须仍然和类内的对应方法名字相同。

3. 如果进程A的一个类上面有一个@ClassId注解，这个类在进程B中对应的接口上有一个相同的@ClassId注解，那么进程A在进程B访问这个类之前必须注册这个类。否则进程B使用Hermes.newInstance()、Hermes.getInstance()或Hermes.getUtilityClass()时，Hermes在进程A中找不到匹配的类。类可以在构造器或者Application.OnCreate()中注册。

   但是，如果类和对应的接口上面没有@ClassId注解，但有相同的包名和类名，那么就不需要注册类。Hermes通过包名和类名匹配类和接口。

   对于接口和类里面的函数，上面的说法仍然适用。

4. 如果你不想让一个类或者函数被其他进程访问，可以在上面加上@WithinProcess注解。

5. 使用Hermes跨进程调用函数的时候，传入参数的类型可以是原参数类型的子类，但不可以是匿名类和局部类。但是回调函数例外，关于回调函数详见“注意事项”的第7点。
   ```
   public class A {}

   public class B extends A {}
   ```
   进程A中有下面这个类：
   ```
   @ClassId(“Foo”)
   public class Foo {
   
       public static A f(A a) {
       }
   }
   ```
   进程B的对应接口如下：
   ```
   @ClassId(“Foo”)
   public interface IFoo {

       A f(A a);
   }
   ```
   进程B中可以写如下代码：
   ```
   IFoo foo = Hermes.getUtilityClass(IFoo.class);
   B b = new B();
   A a = foo.f(b);
   ```

   但你不能写如下代码：
   ```
   A a = foo.f(new A(){});
   ```

6. 如果被调用的函数的参数类型和返回值类型是int、double等基本类型或者String这样的Java通用类型，上面的说法可以很好地解决问题。但如果类型是自定义的类，比如“注意事项”的第5点中的例子，并且两个进程分别属于两个不同app，那么你必须在两个app中都定义这个类，且必须保证代码混淆后，两个类仍然有相同的包名和类名。不过你可以适用@ClassId和@MethodId注解，这样包名和类名在混淆后不同也不要紧了。

7. 如果被调用的函数有回调参数，那么函数定义中这个参数必须是一个接口，不能是抽象类。请特别注意回调函数运行的线程。

   如果进程A调用进程B的函数，并且传入一个回调函数供进程B在进程A进行回调操作，那么默认这个回调函数将运行在进程A的主线程（UI线程）。如果你不想让回调函数运行在主线程，那么在接口声明的函数的对应的回调参数之前加上@Background注解。

   如果回调函数有返回值，那么你应该让它运行在后台线程。如果运行在主线程，那么返回值始终为null。

   默认情况下，Hermes框架持有回调函数的强引用，这个可能会导致内存泄漏。你可以在接口声明的对应回调参数前加上@WeakRef注解，这样Hermes持有的就是回调函数的弱引用。如果进程的回调函数被回收了，而对方进程还在调用这个函数（对方进程并不会知道回调函数被回收），这个不会有任何影响，也不会造成崩溃。如果回调函数有返回值，那么就返回null。

   如果你使用了@Background和@WeakRef注解，你必须在接口中对应的函数参数前进行添加。如果加在其他地方，并不会有任何作用。
   ```
   @ClassId(“Foo”)
   public class Foo {

       public static void f(int i, Callback callback) {
       }
   }

   @ClassId(“callback”)
   public interface Callback {
       void callback();
   }

   @ClassId(“Foo”)
   public interface IFoo {
   
       void f(int i, @WeakRef @Background Callback callback);
   }
   ```
8. 调用函数的时候，任何Context在另一个进程中都会变成对方进程的application context。

9. 数据传输是基于Json的。

10. 使用Hermes框架的时候，有任何的错误，都会使用android.util.Log.e()打出错误日志。你可以通过日志定位问题。

