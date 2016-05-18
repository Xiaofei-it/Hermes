# Hermes
A smart, novel and easy-to-use framework for Android Inter-Process Communication (IPC).

##Advanced Usage

The following will tell you how to let an arbitrary process to provide methods for other processes to invoke.

In each process which provides methods for other processes to invoke, there is a HermesService. It is the service that make the sense. There are 10 such services in Hermes, named from HermesService0 to HermesService9. If this is not enough, you can also extends xiaofei.library.hermes.HermesService to write more HermesService.

###AndroidManifest.xml

Each process which provides methods for other processes to invoke should has its corresponding HermesService declared in AndroidManifest.xml

The following shows the declarations of HermesService1 in Process A and HermesSevice2 in Process B.

```
<service android:name="xiaofei.library.hermes.HermesService$HermesService0">
</service>

<service android:name="xiaofei.library.hermes.HermesService$HermesService1"
    android:process=":a">
</service>

<service android:name="xiaofei.library.hermes.HermesService$HermesService2"
    android:process=":b">
</service>
```

###Initialization

If Process A wants to invoke methods in Process B, it should initialize Hermes as the following:

```
Hermes.connect(getApplicationContext(), HermesService.HermesService2.class);
```

You can call Hermes.isConnected(HermesService.HermesService2.class) to see whether Process B is still alive.

###Instance Creation

In Process A, there are three ways to create instances in Hermes: Hermes.newInstanceInService(), Hermes.getInstanceInService() and Hermes.getUtilityClassInService().

1. Hermes.newInstanceInService(Class<? extends HermesService>, Class<T>, Object...)

   This method creates an new instance of the specified class in Process B and returns the reference to the new instance. The second parameter will be passed into the corresponding constructor of the specified class.
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
   In Process A, you create the instance by Hermes.newInstanceInService(HermesService.HermesService2.class, ILoadingTask.class, “files/image.png”, true) to get the instance of LoadingTask.

2. Hermes.getInstanceInService(Class<? extends HermesService>, Class<T>, Object...)

   This method creates an new instance of the specified class through its static method named “getInstance” in Process B and returns the reference to the new instance. The second parameter will be passed into the corresponding static “getInstance” method of the specified class. This is useful when the specified class is a singleton.
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
   In Process A, you create the instance by Hermes.getInstanceInService(HermesService.HermesService2.class, IBitmapWrapper.class, “files/image.png”) or Hermes.getInstanceInService(HermesService.HermesService2.class, IBitmapWrapper.class, 1001) to get the instance of BitmapWrapper.

3. Hermes.getUtilityClassInService(Class<? extends HermesService>, Class<T>)

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
   In Process A, you can do as the below:
   ```
   IMaths maths = Hermes.getUtilityClassInService(HermesService.HermesService2.class, IMaths.class);
   int sum = maths.plus(3, 5);
   int diff = maths.minus(3, 5);
   ```
