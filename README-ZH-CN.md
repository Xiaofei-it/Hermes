# Hermes
一套新颖巧妙易用的Android进程间通信IPC框架。

Hermes是一套新颖巧妙易用的Android进程间通信IPC框架。这个框架使得你不用了解IPC机制就可以进行进程间通信，像调用本地函数一样调用其他进程的函数。

##特色

1. 使得进程间通信像调用本地函数一样方便简单。

2. 轻而易举在本地进程创建其他进程类的对象，轻而易举在本进程获取其他进程的单例，轻而易举在本进程使用其他进程的工具类。

3. 支持进程间函数回调，调用其他进程函数的时候可以传入回调函数，让其他进程回调本进程的方法。

4. 自带内存优化，并且支持跨进程垃圾回收。


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
    compile 'xiaofei.library:hermes:0.2'
}
```

##Maven

```
<dependency>
  <groupId>xiaofei.library</groupId>
  <artifactId>hermes</artifactId>
  <version>0.2</version>
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

###初始化

经常地，一个app有一个, an app has a default process in which most components run. Name this default process Process A.

Suppose there is another process, named Process B. Process B wants to invoke methods in Process A. Then Process B should initialize Hermes at the beginning.

You can do this in Application.OnCreate() or Activity.OnCreate() in Process B. The Corresponding API is Hermes.connect(Context). Before initialization, a HermesListener can be set to do some callbacks.

```
Hermes.connect(getApplicationContext());
```

You can call Hermes.isConnected() to see whether the process you are communicating with is still alive.

###Context Setting

In the process which provides methods for other processes to invoke, you can use Hermes.setContext(Context) to set the context.

The context will be used when the Context parameter is passed in from other processes. See Point 8 of Notice.

###Registration

In Process A, classes to be accessed by Process B should be registered before being accessed. There are two APIs to register classes: Hermes.register(Class<?>) and Hermes.register(Object). Hermes.register(object) is equivalent to Hermes.register(object.getClass()).

Registration is, however, not necessary if you do not add annotations on the classes. See Point 3 of Notice.

###Instance Creation

In Process B, there are three ways to create instances in Hermes: Hermes.newInstance(), Hermes.getInstance() and Hermes.getUtilityClass().

1. Hermes.newInstance(Class<T>, Object...)

   This method creates an new instance of the specified class in Process A and returns the reference to the new instance. The second parameter will be passed into the corresponding constructor of the specified class.
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
   In Process B, you create the instance by Hermes.newInstance(ILoadingTask.class, “files/image.png”, true) to get the instance of LoadingTask.

2. Hermes.getInstance(Class<T>, Object...)

   This method creates an new instance of the specified class through its static method named “getInstance” in Process A and returns the reference to the new instance. The second parameter will be passed into the corresponding static “getInstance” method of the specified class. This is useful when the specified class is a singleton.
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
   In Process B, you create the instance by Hermes.getInstance(IBitmapWrapper.class, “files/image.png”) or Hermes.getInstance(IBitmapWrapper.class, 1001) to get the instance of BitmapWrapper.

3. Hermes.getUtilityClass(Class<T>)

   This method provides a way to use the utility class in another process. (This is very useful when developing plugins.)
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
   In Process B, you can do as the below:
   ```
   IMaths maths = Hermes.getUtilityClass(IMaths.class);
   int sum = maths.plus(3, 5);
   int diff = maths.minus(3, 5);
   ```

##Notice

1. Actually, if two processes are in separate apps, named App A and App B respectively, and App A wants to access a class in App B, and if the interface of App A and the corresponding class of App B has the same name and the same package name, then there is no need to add the ClassId annotation on them. But pay more attention when you use ProGuard, since the names of the corresponding classes will be different after obfuscating.

2. If the method in an interface and the method in the corresponding class have the same name, there is also no need to add the MethodId annotation on them.

3. If a class in Process A has a ClassId annotation presented on it and its corresponding interface in Process B has also a ClassId annotation with the same value presented on it, the class should be registered before being accessed by Process B. Otherwise, when Process B uses Hermes.newInstance(), Hermes.getInstance() or Hermes.getUtilityClass(), Hermes will not find the matching class in Process A. A class can be registered in its constructor or in Application.OnCreate().

   However, if the class and its corresponding interface do not have an annotation presented on them but have the same name and the same package name, then there is no need to register the class. Hermes finds the class according to the package and the name.

   The above also works when it comes to the method.

4. If you want to prevent a class or a method from being accessed from outside the process, add a WithinProcess annotation on it.

5. The type of the parameters you pass into the method can be a subclass of the corresponding parameter type, but cannot be an anonymous class or a local class. But callback is supported. See Point 7 for more information about callbacks.
   ```
   public class A {}

   public class B extends A {}
   ```
   In Process A, there is a class as below:
   ```
   @ClassId(“Foo”)
   public class Foo {
   
       public static A f(A a) {
       }
   }
   ```
   Then in Process B, the interface is as below:
   ```
   @ClassId(“Foo”)
   public interface IFoo {

       A f(A a);
   }
   ```
   In Process B, you can write the followings:
   ```
   IFoo foo = Hermes.getUtilityClass(IFoo.class);
   B b = new B();
   A a = foo.f(b);
   ```

   You can NOT write the following:
   ```
   A a = foo.f(new A(){});
   ```

6. If the parameter types and the return type of the invoked method are the primitive types or some common classes such as String, the above will work very well. However, if they are the classes you declare, as the example in Point 5 shows, and if the method invocation is between apps, then you must declare the classes in both App A and App B. What’s more, you should guarantee that the name of the class and its methods remain the same even after you use the ProGuard. Or you can add the ClassId annotation on the class and the MethodId annotation on the methods.

7. If the invoked method has some callback parameters, then the parameter type must be an interface. It can NOT be an abstract class. Attention should be paid when it comes to the thread where the callback run.

   If Process A invokes a method in Process B, and passes some callbacks as the parameters of the method to let Process B perform some callback operations in Process A, the callback will run, by default, in the main thread, also known as the UI thread. If you want to let the callback run in the background, you can add the Background annotation before the corresponding parameter when you declare the interface.

   If the callback has an return value, it should run in the background. If it runs in the main thread, the return value will always be null.

   By default, Hermes holds a strong reference to the callback, which may cause the memory leak. You can add a WeakRef annotation before the corresponding callback parameter to let Hermes hold a weak reference to the callback. If the callback in a process has been reclaimed and it is called from the other process (A process does not know the callback in another process has been reclaimed), nothing happens and if the callback has a return value, the return value will be null.

   If you want to use the Background annotation and the WeakRef annotation, you should add them when you declare the interface. If you add them elsewhere, it will take no effect.
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
8. Any context passed into the invoked method as the parameter will be replaced by the application context of the remote process.

9. Data transmitting between processes is based on Json.

10. If any error occurs, a error log will be printed by android.util.Log.e(). You can see the log for the detail of the error.

