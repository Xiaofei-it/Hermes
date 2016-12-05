# Hermes
A smart, novel and easy-to-use framework for Android Inter-Process Communication (IPC).

[Chinese Readme 中文文档](https://github.com/Xiaofei-it/Hermes/blob/master/README-ZH-CN.md)

Hermes is a smart, novel and easy-to-use framework for Android Inter-Process Communication (IPC). In this framework, you can use IPC even if you do not understand the underneath principle of Android IPC.

Please click [HERE](https://github.com/Xiaofei-it/Hermes-IPC-Demo) to see the demo.

Also, [HermesEventBus](https://github.com/eleme/HermesEventBus) is a Hermes-and-EventBus-based library
which posts events between processes.

Note that maybe you will find that Hermes is still a little difficult to use. I have finished this
library and promoted the performance. What to do for my next step is to simplify the usage, which
will be released in version 0.7.0.

##Features

1. Make method invocations over IPC so easy just like method invocations in a local process. The statements of method invocations are almost the same.

2. Easy to use classes, singletons, utilities in another process.

3. Support callbacks over IPC. You can provide the remote process with a callback to perform operations in the local process.

4. Support GC over IPC. Hermes contains two garbage collectors to reclaim the instances created in the remote process and the local callbacks for remote processes.


##Principle

Since the main purpose of IPC is to invoke methods in another process, Hermes makes method invocation so easy that you can invoke methods in another process just like you invoke methods in a local process, and also, the statements of method invocation in another process are almost the same as the ones in a local process.

For example, the singleton pattern is always used in Android apps. Suppose an app contains two processes and they want to use a singleton. The singleton is as below:
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
Suppose the instance of Singleton is in Process A, and you want to access it from Process B. Then you write an interface as below:
```
@ClassId(“Singleton”)
public interface ISingleton {

    @MethodId(“setData”)
    void setData(String data);

    @MethodId(“getData”)
    String getData();

}
```
And the statements to access the singleton in Process B is as below:
```
//obtain the instance of Singleton
ISingleton singleton = Hermes.getInstance(ISingleton.class);

//Set a data
singleton.setData(“Hello, Hermes!”);

//Get the data
Log.v(TAG, singleton.getData());
```
Amazing, isn’t it?

Specifically, you pass an interface into Hermes.getInstance() and Hermes.getInstance() returns an instance which is exactly the same as the original one in Process A. Then when you invoke a method on the instance in Process B, the method is invoked on the same instance in Process A.

Now, how to write the interface? So easy. For example, there is a class named Foo in Process A, and you want to access it from Process B. Then you write an interface named IFoo and add into the interface the signatures of the methods which you want to invoke on Foo. Then add the same @ClassId annotation with the same value on IFoo and Foo, and add the same @MethodId annotation with the same value on the corresponding methods. Done! At this time, you can use Hermes.getInstance(IFoo.class) in Process B to get the instance of Foo in Process A.

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

##Usage

The following will tell you how to let the default process of the app to provide methods for other processes to invoke.

Hermes also allows method invocation between arbitrary processes. For more information about how to do this, please see [HERE](https://github.com/Xiaofei-it/Hermes/blob/master/AdvancedTutorial.md).

###AndroidManifest.xml

```
<service android:name="xiaofei.library.hermes.HermesService$HermesService0">
</service>
```

###Initialization

Always, an app has a default process in which most components run. Name this default process Process A.

Process A should initialize Hermes in Application.OnCreate() or Activity.OnCreate(), as the following does:

```
Hermes.init(getApplicationContext());
```

###Connection

Suppose there is another process, named Process B. Process B wants to invoke methods in Process A. Then Process B should initialize Hermes at the beginning.

You can do this in Application.OnCreate() or Activity.OnCreate() in Process B. The Corresponding API is Hermes.connect(Context). Before initialization, a HermesListener can be set to do some callbacks.

```
Hermes.connect(getApplicationContext());
```

You can call Hermes.isConnected() to see whether the process you are communicating with is still alive.

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

1. Actually, if two processes are in separate apps, named App A and App B respectively, and App A wants to access a class in App B, and if the interface of App A and the corresponding class of App B has the same name and the same package name, then there is no need to add the @ClassId annotation on them. But pay more attention when you use ProGuard, since the names of the corresponding classes will be different after obfuscating.

2. If the method in an interface and the method in the corresponding class have the same name, there is also no need to add the @MethodId annotation on them.

3. If a class in Process A has a @ClassId annotation presented on it and its corresponding interface in Process B has also a @ClassId annotation with the same value presented on it, the class should be registered in Process A before being accessed by Process B. Otherwise, when Process B uses Hermes.newInstance(), Hermes.getInstance() or Hermes.getUtilityClass(), Hermes will not find the matching class in Process A. A class can be registered in its constructor or in Application.OnCreate().

   However, if the class and its corresponding interface do not have an annotation presented on them but have the same name and the same package name, then there is no need to register the class. Hermes finds the class according to the package and the name.

   The above also works when it comes to the method.

4. If you want to prevent a class or a method from being accessed from outside the process, add a @WithinProcess annotation on it.

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

6. If the parameter types and the return type of the invoked method are the primitive types or some common classes such as String, the above will work very well. However, if they are the classes you declare, as the example in Point 5 shows, and if the method invocation is between apps, then you must declare the classes in both App A and App B. What’s more, you should guarantee that the name of the class and its methods remain the same even after you use the ProGuard. Or you can add the @ClassId annotation on the class and the @MethodId annotation on the methods.

7. If the invoked method has some callback parameters, then the parameter type must be an interface. It can NOT be an abstract class. Attention should be paid when it comes to the thread where the callback run.

   If Process A invokes a method in Process B, and passes some callbacks as the parameters of the method to let Process B perform some callback operations in Process A, the callback will run, by default, in the main thread, also known as the UI thread, of Process A. If you want to let the callback run in the background, you can add the @Background annotation before the corresponding parameter when you declare the interface.

   If the callback has an return value, you should let it run in the background. If it runs in the main thread, the return value will always be null.

   By default, Hermes holds a strong reference to the callback, which may cause the memory leak. You can add a @WeakRef annotation before the corresponding callback parameter to let Hermes hold a weak reference to the callback. If the callback in a process has been reclaimed and it is called from the other process (A process does not know the callback in another process has been reclaimed), nothing happens and if the callback has a return value, the return value will be null.

   If you want to use the @Background annotation and the @WeakRef annotation, you should add them when you declare the interface. If you add them elsewhere, it will take no effect.
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

