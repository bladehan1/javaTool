package reflect;


import com.google.common.reflect.TypeParameter;
import reflect.example.GExample;
import reflect.example.GIExample;
import reflect.example.User;

import java.lang.reflect.*;
import java.security.ProtectionDomain;
import java.util.*;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.out;

@Annotation1
public class ReflectUtil {

    public static void main(String[] args) {
        GExample<User> userGExample=new GExample<User>();
        logClass(userGExample.getClass());
//        classSyntaxTest();
//        getClassTest();
//        forNameTest();
//        methodGetClass();
//          String[] strings={"[Ljava.lang.String;","java.util.concurrent.ConcurrentNavigableMap","java.io.InterruptedIOException"};
//          classModify(strings);
//       logAll(getOneObject(int.class));
    }


    public static void totalTest(){
        classSyntaxTest();
        getClassTest();
        logAllTest();
        methodGetClass();
    }

    public static Map<Class,Class> initClassMap(){
        Map<Class,Class> classMap=new TreeMap<>();
        classMap.put(List.class,ArrayList.class);
        classMap.put(Map.class,TreeMap.class);
        return classMap;
    }

    public static Class getMapClass(Class c){
       return initClassMap().get(c)==null?c: initClassMap().get(c);
    }

    public static <T> Object getOneObject(Class<T> c){
        if(c==null){
            return null;
        }
        //boolean`, `byte`, `short`, `int`, `long`, `char`, `float`, and `double`
        //反射难以转为泛型类型,只能返回object 实际为包装类
        if(c.isPrimitive()){
            String className=c.getName();
            switch(className){
                case "boolean":
                    return c.cast(false);
                case "byte":
                case "short":
                case "int":
                case "long":
                    return 0;
                case "char":
                    return "";
                case "float":
                case "double":
                    return 0.0;
                default:
                    return null;
            }
        }else if(c.isEnum()){
           T[] enumConstants =c.getEnumConstants();
           if(enumConstants.length>0){
               return enumConstants[0];
           }else{
               return null;
           }
        }else if(c.isArray()){
         Class componentType=   c.getComponentType();
         Object o = Array.newInstance(componentType, 1);
         Array.set(o, 0, getOneObject(componentType));
        }else if (c.isAnnotation()){
            return null;
        }
        //引用类型,类(实体类，抽象类);接口,enum,数组
        //抽象类和接口如何实例化
        c=getMapClass(c);
        Constructor<T> constructor = null;
        T t=null;
        try {
             //如果是接口类型,需要找到其中一个实例类。甚至创建一个实例类！！！
             //创建一个类型映射，映射接口和抽象类名字到实现类
             constructor= c.getConstructor();
             constructor.setAccessible(true);
             t=constructor.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(-1);
        }catch (InvocationTargetException e) {
            e.printStackTrace();
            System.exit(-1);
        }catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(-1);
        }catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return t;
    }

    private static void fillField(Object obj){
        if(obj==null){
            return ;
        }
        Class c=obj.getClass();
        if(c.isPrimitive()){
            return;
        }
       Field[] fields=c.getFields();
       Arrays.asList(fields).forEach(p->{
           String name=p.getName();
           Class<?> fc=p.getType();
           //Integer 无法转换为int
           p.setAccessible(true);
           try {
               p.set(obj,getOneObject(fc));
           } catch (IllegalAccessException e) {
               e.printStackTrace();
           }
       });
    }

    /**
     * .class 语法测试
     */
    public static void classSyntaxTest() {
        //.class 语法
        Class cc1 = int.class;
        Class cc2 = int[].class;
        Class cc3 = Integer.class;
        Class cc4 = Inter1.class;
        Class cc5 = Enum1.class;
//        Class cc6=null.class;
        Class cc7 = Annotation1.class;
        Class[] cArray={cc1,cc2,cc3,cc4,cc5,cc7};
        logAll(cArray);
    }

    /**
     * getClass 函数测试
     */
    public static void getClassTest() {
        //原始类型
//        int i = 1;
        //数组类型
        int[] iArray = {1};
        //引用类型
        Integer i2 = 1;
        //接口
        Inter1 inter1 = new Inter1() {
        };
        //enum
        Enum1 enum1 = Enum1.A;
        //Null;
//        Object null1 = null;
        //annotation1
        Annotation1 annotation1 = enum1.getClass().getAnnotation(Annotation1.class);

        //getClass(); 对象发放
//        Class c1=i.getClass();
        Class c2 = iArray.getClass();
        Class c3 = i2.getClass();
        Class c4 = inter1.getClass();
        Class c5 = enum1.getClass();
//        Class c6=null1.getClass();
        Class c7 = annotation1.getClass();
        Class[] cArray={c2,c3,c4,c5,c7};
        logAll(cArray);

    }

    /**
     * Class.forName 函数测试
     */
//    public static void forNameTest() {
//        //
//        try {
////            Class cf1=Class.forName("int");
//            Class c2 = Class.forName("[I");
////              Class c3=Class.forName("java.lang.Integer'");
//            Class cDoubleArray = Class.forName("[D");
//
//            Class cStringArray = Class.forName("[[Ljava.lang.String;");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public static void methodGetClass() {
        Class<?>[] c1 = Character.class.getClasses();
        logAll(c1);
        //[Character.Subset and Character.UnicodeBlock]
        Class<?>[] c2 = Character.class.getDeclaredClasses();
        logAll(c2);
        try {
            Field f = System.class.getField("out");
            Class c3 = f.getDeclaringClass();
            logAll(c3);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Object o = new Object() {
            public void m() {}
        };
         Class c4= o.getClass().getEnclosingClass();
         logAll(c4);


    }

    public static void logAllTest(){
        int i = 1;
        Integer i1 = 1;
        Map<String, String> map = new TreeMap<>();
        logAll(i);
        logAll(i1);
        logAll(map);
    }
    public static void logClass(Class c){
       boolean isInterface= c.isInterface();
       System.out.println(c.toString());
       System.out.println(c.toGenericString());
        //interface 还是abstract ||泛型,class的全部
     Type[] genericInterfaces=   c.getGenericInterfaces();
     System.out.println("genericInterfaces:"+genericInterfaces.toString());
     Class<?> componentType =  c.getComponentType();
     System.out.println("componentType:"+componentType);
     Object[] enumConstants  =c.getEnumConstants();
     System.out.println("enumConstants:"+enumConstants);
     TypeVariable[] typeparms    =c.getTypeParameters();
     if(typeparms.length>0) {
         StringBuilder sb=new StringBuilder();
         if (typeparms.length > 0) {
             boolean first = true;
             sb.append('<');
             for(TypeVariable<?> typeparam: typeparms) {
                 if (!first)
                     sb.append(',');
                 Class typeC=  typeparam.getClass();//获取泛型类型。
                 sb.append(typeC.toGenericString()+":");
                 //尝试全部typeparam 方法
                 sb.append(typeparam.getTypeName());
                 first = false;
             }
             sb.append('>');
             System.out.println("typeparms_sb:"+sb.toString());
         }

         System.out.println("typeparms:" + typeparms);
     }
      String typeName = c.getTypeName();
        System.out.println("typeName:"+typeName);
      String name=  c.getName();
        System.out.println("name:"+name);
      String  canonicalName=c.getCanonicalName();
        System.out.println("canonicalName:"+canonicalName);
      Class[] classes=  c.getClasses();
        System.out.println("classes:"+classes);

      Class c1 = c.getClass();
        System.out.println("c1:"+c1);
      ClassLoader classLoader =c.getClassLoader();
        System.out.println("classLoader:"+classLoader);
       Class[] declaredClasses =c.getDeclaredClasses();
        System.out.println("declaredClasses:"+declaredClasses);
       Class declaringClass =c.getDeclaringClass();
        System.out.println("declaringClass:"+declaringClass);
       Type genericSuperclass= c.getGenericSuperclass();
        System.out.println("genericSuperclass:"+genericSuperclass);
        Class[] interfaces=c.getInterfaces();
        System.out.println("interfaces:"+interfaces);
       int modifiers= c.getModifiers();
        System.out.println("modifiers:"+modifiers);
       Package aPackage= c.getPackage();
        System.out.println("aPackage:"+aPackage);
      ProtectionDomain protectionDomain =c.getProtectionDomain();
        System.out.println("protectionDomain:"+protectionDomain);
//        c.getResourceAsStream();
       Object[] signers =c.getSigners();
        System.out.println("signers:"+signers);
       String simpleName= c.getSimpleName();
        System.out.println("simpleName:"+simpleName);
    }
    public static <T> void logAll(T obj) {
        String ret = "";
        if (null == obj) {
            ret += "is Null\n";
        } else if(isPrivateType(obj)) {
            //原始类型
            ret+=obj.toString()+"\n";
            ret+=obj.getClass().getName()+"\n";
        }else{
            if(obj instanceof  Class){
               TypeVariable[] list = ((Class) obj).getTypeParameters();
                //class类型
                Class c=(Class)obj;
                ret+="originClass:"+c.getCanonicalName()+"\n";
                ret+="originClass:"+c.getName()+"\n";
            }
            //引用类型
            Class c =obj.getClass();
            ret+="class:"+c.getCanonicalName()+"\n";

            //数组类型
            if(c.isArray()){
                //array的元素
                //截取元素个数
                ret += Arrays.stream((T[]) obj).limit(100).map(p -> p.toString()).reduce((p, q) -> p + "," + q);
            }else if(obj instanceof  Collection){
                //Collection元素
                //截取元素个数
                ret+=((Collection)obj).stream().limit(100).map(p->p.toString()).reduce((p,q)->p+","+q);
            }
        }
        System.out.println(ret);
    }

    public static <T> boolean isPrivateType(T obj) {
        //boolean`, `byte`, `short`, `int`, `long`, `char`, `float`, and `double,void
        if (obj instanceof Boolean ||
                obj instanceof Byte ||
                obj instanceof Short ||
                obj instanceof Integer ||
                obj instanceof Long ||
                obj instanceof Character ||
                obj instanceof Float ||
                obj instanceof Double ||
                obj instanceof Void
        ) {
            return true;
        } else {

            return false;
        }
    }

    public static  void classModify(String... args) {
        try {
            Class<?> c = Class.forName(args[0]);
            out.format("Class:%n  %s%n%n", c.getCanonicalName());
            out.format("Modifiers:%n  %s%n%n",
                    Modifier.toString(c.getModifiers()));

            out.format("Type Parameters:%n");
            TypeVariable[] tv = c.getTypeParameters();
            if (tv.length != 0) {
                out.format("  ");
                for (TypeVariable t : tv)
                    out.format("%s ", t.getName());
                out.format("%n%n");
            } else {
                out.format("  -- No Type Parameters --%n%n");
            }

            out.format("Implemented Interfaces:%n");
            Type[] intfs = c.getGenericInterfaces();
            if (intfs.length != 0) {
                for (Type intf : intfs)
                    out.format("  %s%n", intf.toString());
                out.format("%n");
            } else {
                out.format("  -- No Implemented Interfaces --%n%n");
            }

            out.format("Inheritance Path:%n");
            List<Class> l = new ArrayList<Class>();
            printAncestor(c, l);
            if (l.size() != 0) {
                for (Class<?> cl : l)
                    out.format("  %s%n", cl.getCanonicalName());
                out.format("%n");
            } else {
                out.format("  -- No Super Classes --%n%n");
            }

            out.format("Annotations:%n");
            Annotation[] ann = c.getAnnotations();
            if (ann.length != 0) {
                for (Annotation a : ann)
                    out.format("  %s%n", a.toString());
                out.format("%n");
            } else {
                out.format("  -- No Annotations --%n%n");
            }

            // production code should handle this exception more gracefully
        } catch (ClassNotFoundException x) {
            x.printStackTrace();
        }
    }

    private static void printAncestor(Class<?> c, List<Class> l) {
        Class<?> ancestor = c.getSuperclass();
        if (ancestor != null) {
            l.add(ancestor);
            printAncestor(ancestor, l);
        }
    }

}
