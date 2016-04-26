package xiaofei.library.hermes.util;

import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.HashMap;

import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;
import xiaofei.library.hermes.wrapper.BaseWrapper;
import xiaofei.library.hermes.wrapper.MethodWrapper;

/**
 * Created by Xiaofei on 16/4/7.
 */
public class TypeCenter {
    
    private static TypeCenter sInstance = null;

    private HashMap<String, Class<?>> mAnnotatedClasses;

    private HashMap<String, Class<?>> mRawClasses;

    private HashMap<Class<?>, HashMap<String, Method>> mAnnotatedMethods;

    private HashMap<Class<?>, HashMap<String, Method>> mRawMethods;

    private TypeCenter() {
        mAnnotatedClasses = new HashMap<String, Class<?>>();
        mRawClasses = new HashMap<String, Class<?>>();
        mAnnotatedMethods = new HashMap<Class<?>, HashMap<String, Method>>();
        mRawMethods = new HashMap<Class<?>, HashMap<String, Method>>();
    }

    public static synchronized TypeCenter getInstance() {
        if (sInstance == null) {
            sInstance = new TypeCenter();
        }
        return sInstance;
    }

    private void registerClass(Class<?> clazz) {
        ClassId classId = clazz.getAnnotation(ClassId.class);
        if (classId == null) {
            synchronized (mRawClasses) {
                String className = clazz.getName();
                if (!mRawMethods.containsKey(className)) {
                    mRawClasses.put(className, clazz);
                }
            }
        } else {
            synchronized (mAnnotatedClasses) {
                String className = classId.value();
                if (!mAnnotatedClasses.containsKey(className)) {
                    mAnnotatedClasses.put(className, clazz);
                }
            }
        }
    }

    private void registerMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            MethodId methodId = method.getAnnotation(MethodId.class);
            if (methodId == null) {
                synchronized (mRawMethods) {
                    if (!mRawMethods.containsKey(clazz)) {
                        mRawMethods.put(clazz, new HashMap<String, Method>());
                    }
                    HashMap<String, Method> map = mRawMethods.get(clazz);
                    String key = TypeUtils.getMethodId(method);
                    map.put(key, method);
                }
            } else {
                synchronized (mAnnotatedMethods) {
                    if (!mAnnotatedMethods.containsKey(clazz)) {
                        mAnnotatedMethods.put(clazz, new HashMap<String, Method>());
                    }
                    HashMap<String, Method> map = mAnnotatedMethods.get(clazz);
                    String key = TypeUtils.getMethodId(method);
                    map.put(key, method);
                }
            }
        }
    }

    public void register(Class<?> clazz) {
        TypeUtils.validateClass(clazz);
        registerClass(clazz);
        registerMethod(clazz);
    }

    public Class<?> getClassType(BaseWrapper wrapper) throws HermesException {
        String name = wrapper.getName();
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (wrapper.isName()) {
            Class<?> clazz = mRawClasses.get(name);
            if (clazz != null) {
                return clazz;
            }
            //boolean, byte, char, short, int, long, float, and double void
            if (name.equals("boolean")) {
                clazz = boolean.class;
            } else if (name.equals("byte")) {
                clazz = byte.class;
            } else if (name.equals("char")) {
                clazz = char.class;
            } else if (name.equals("short")) {
                clazz = short.class;
            } else if (name.equals("int")) {
                clazz = int.class;
            } else if (name.equals("long")) {
                clazz = long.class;
            } else if (name.equals("float")) {
                clazz = float.class;
            } else if (name.equals("double")) {
                clazz = double.class;
            } else if (name.equals("void")) {
                clazz = void.class;
            } else {
                try {
                    clazz = Class.forName(name);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new HermesException(ErrorCodes.CLASS_NOT_FOUND,
                            "Cannot find class " + name + ". Classes without ClassId annotation on it "
                                    + "should be located at the same package and have the same name, "
                                    + "EVEN IF the source code has been obfuscated by Proguard.");
                }

            }
            mRawClasses.put(name, clazz);
            return clazz;
        } else {
            Class<?> clazz = mAnnotatedClasses.get(name);
            if (clazz == null) {
                throw new HermesException(ErrorCodes.CLASS_NOT_FOUND,
                        "Cannot find class with ClassId annotation on it. ClassId = " + name
                                + ". Please add the same annotation on the corresponding class in the remote process.");
            }
            return clazz;
        }
    }

    public Class<?>[] getClassTypes(BaseWrapper[] wrappers) throws HermesException {
        Class<?>[] classes = new Class<?>[wrappers.length];
        for (int i = 0; i < wrappers.length; ++i) {
            classes[i] = getClassType(wrappers[i]);
        }
        return classes;
    }

    public Method getMethod(Class<?> clazz, MethodWrapper methodWrapper) throws HermesException {
        String name = methodWrapper.getName();
        if (methodWrapper.isName()) {
            Class<?> returnType = getClassType(methodWrapper.getReturnType());
            if (!mRawMethods.containsKey(clazz)) {
                mRawMethods.put(clazz, new HashMap<String, Method>());
            }
            Method method = mRawMethods.get(clazz).get(name);
            if (method != null) {
                Class<?> tmp = method.getReturnType();
                if (TypeUtils.primitiveMatch(tmp, returnType)) {
                    return method;
                }
                if (tmp != returnType) {
                    throw new HermesException(ErrorCodes.METHOD_NOT_FOUND,
                            "The return type of methods do not match. "
                                    + "Method " + method + " return type: " + tmp.getName()
                                    + ". The required is " + returnType.getName());
                }
                return method;
            }
            int pos = name.indexOf('(');
            method = TypeUtils.getMethod(clazz, name.substring(0, pos), getClassTypes(methodWrapper.getParameterTypes()), getClassType(methodWrapper.getReturnType()));
            if (method == null) {
                throw new HermesException(ErrorCodes.METHOD_NOT_FOUND,
                        "Method not found: " + name + " in class " + clazz.getName());
            }
            mRawMethods.get(clazz).put(name, method);
            return method;
        } else {
            HashMap<String, Method> methods = mAnnotatedMethods.get(clazz);
            //TODO
            Method method = methods.get(name);
            if (method != null) {
                return method;
            }
            throw new HermesException(ErrorCodes.METHOD_NOT_FOUND,
                    "Method not found in class " + clazz.getName() + ". Method id = " + name + ". "
                            + "Please add the same annotation on the corresponding method in the remote process.");
        }
    }
}
