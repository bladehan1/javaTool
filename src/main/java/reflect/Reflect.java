package reflect;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.util.*;

import static java.lang.System.out;
import static java.lang.System.err;

import java.io.Console;
import java.nio.charset.Charset;

public class Reflect {


    public static void main(String[] args) {
         getFieldTypeTest();
//        searchFieldModifierTest();
//        setFieldTest();
//          methodInfoTest();
//        showClassMethodTest();
//        methodParsingTest();
//        methodInvokeTest();
//        searchFieldTest();
//        useConstruct();
    }


    public static void getFieldTypeTest() {
        getFieldType(FieldSpe.class, "b");
        getFieldType(FieldSpe.class, "name");
        getFieldType(FieldSpe.class, "list");
        getFieldType(FieldSpe.class, "val");
    }

    public static void searchFieldModifierTest() {
        searchFieldModifier(FieldModifierSpy.class, "volatile");
        searchFieldModifier(Spy.class, "public");
        searchFieldModifier(FieldModifierSpy.Inner.class, "final");
        searchFieldModifier(Spy.class, "private", "static", "final");

    }

    ///////////////////method//////////////
    public static void methodInfoTest() {
        methodInfo(Class.class, "getConstructor");
        methodInfo(Class.class, "cast");
        methodInfo(PrintStream.class, "format");
    }

    public static void showClassMethodTest() {
        MethodParameterSpy.printClass(ExampleMethods.class);
    }

    public static void methodParsingTest(){
        methodParsing(Object.class,"wait");
        methodParsing(StrictMath.class,"toRadians");
        methodParsing(Reflect.class,"inc");
        methodParsing(Class.class,"getConstructor");
        methodParsing(String.class,"compareTo");
    }

    //////////////////construct//////////////////

    public static void constructSearchParameterTest() {
        constructSearchParameter(Formatter.class, Locale.class);
        constructSearchParameter(String.class, char[].class);
        constructSearchParameter(ProcessBuilder.class, String[].class);
        constructSearchParameter(HashMap.class, Map.class);
    }

    public static void searchModifierTest() {
//        constructSearchModifier(File.class,Modifier.PUBLIC);
        constructSearchModifier(SyntheticConstructor.class, 0);
    }

    public static void searchFieldTest() {
        searchField();
    }

    /**
     * 查找类1中含有指定类型的构造函数
     *
     * @param c
     * @param cArg
     */
    public static void constructSearchParameter(Class<?> c, Class<?> cArg) {

        Constructor[] allConstructors = c.getDeclaredConstructors();
//            ReflectUtil.logAll(cArg);
//            ReflectUtil.logAll(c);
//            ReflectUtil.logAll(allConstructors);
        for (Constructor ctor : allConstructors) {
            Class<?>[] pType = ctor.getParameterTypes();
            for (int i = 0; i < pType.length; i++) {
                if (pType[i].equals(cArg)) {
                    out.format("%s%n", ctor.toGenericString());

                    Type[] gpType = ctor.getGenericParameterTypes();
                    for (int j = 0; j < gpType.length; j++) {
                        char ch = (pType[j].equals(cArg) ? '*' : ' ');
                        out.format("%7c%s[%d]: %s%n", ch,
                                "GenericParameterType", j, gpType[j]);
                    }
                    break;
                }
            }
        }
        // production code should handle this exception more gracefully

    }


    ///////////////////////////字段//////////////////////////
    public static void getFieldType(Class<?> c, String field) {
        try {
            Field f = c.getField(field);
            //返回运行时类型 泛型 运行时擦除，返回object
            System.out.format("Type: %s%n", f.getType());
            //返回类型标识 泛型 T
            System.out.format("GenericType: %s%n", f.getGenericType());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public static void searchFieldModifier(Class<?> c, String... modifier) {

        int searchMods = 0x0;
        for (int i = 0; i < modifier.length; i++) {
            searchMods |= modifierFromString(modifier[i]);
        }

        Field[] flds = c.getDeclaredFields();
        out.format("Fields in Class '%s' containing modifiers:  %s%n",
                c.getName(),
                Modifier.toString(searchMods));
        boolean found = false;
        for (Field f : flds) {
            int foundMods = f.getModifiers();
            // Require all of the requested modifiers to be present
            if ((foundMods & searchMods) == searchMods) {
                out.format("%-8s [ synthetic=%-5b enum_constant=%-5b ]%n",
                        f.getName(), f.isSynthetic(),
                        f.isEnumConstant());
                found = true;
            }
        }

        if (!found) {
            out.format("No matching fields%n");
        }

        // production code should handle this exception more gracefully

    }

    public static void setFieldTest() {
        Book book = new Book();
        String fmt = "%6S:  %-12s = %s%n";

        try {
            Class<?> c = book.getClass();

            Field chap = c.getDeclaredField("chapters");
            out.format(fmt, "before", "chapters", book.chapters);
            chap.setLong(book, 12);
            out.format(fmt, "after", "chapters", chap.getLong(book));

            Field chars = c.getDeclaredField("characters");
            chars.setAccessible(true);
            out.format(fmt, "before", "characters",
                    Arrays.asList(book.getCharacters()));
            String[] newChars = {"Queen", "King"};
            chars.set(book, newChars);
            out.format(fmt, "after", "characters",
                    Arrays.asList(book.getCharacters()));

            Field t = c.getDeclaredField("twin");
            out.format(fmt, "before", "twin", book.twin);
            t.set(book, Tweedle.DUM);
            out.format(fmt, "after", "twin", t.get(book));

            // production code should handle these exceptions more gracefully
        } catch (NoSuchFieldException x) {
            x.printStackTrace();
        } catch (IllegalAccessException x) {
            x.printStackTrace();
        }
    }
    ////////////////////////////////函数////////////////////////

    public static void methodInfo(Class<?> c, String methodName) {
        String fmt = "%24s: %s%n";
        Method[] allMethods = c.getDeclaredMethods();
        for (Method m : allMethods) {
            if (!m.getName().equals(methodName)) {
                continue;
            }
            out.format("%s%n", m.toGenericString());

            out.format(fmt, "ReturnType", m.getReturnType());
            out.format(fmt, "GenericReturnType", m.getGenericReturnType());

            Class<?>[] pType = m.getParameterTypes();
            Type[] gpType = m.getGenericParameterTypes();
            for (int i = 0; i < pType.length; i++) {
                out.format(fmt, "ParameterType", pType[i]);
                out.format(fmt, "GenericParameterType", gpType[i]);
            }

            Class<?>[] xType = m.getExceptionTypes();
            Type[] gxType = m.getGenericExceptionTypes();
            for (int i = 0; i < xType.length; i++) {
                out.format(fmt, "ExceptionType", xType[i]);
                out.format(fmt, "GenericExceptionType", gxType[i]);
            }
        }
    }
    private static int count;

    private static synchronized void inc() {
        count++;
    }

    private static synchronized int cnt() {
        return count;
    }

    public static void methodParsing(Class<?> c, String methodName) {
        Method[] allMethods = c.getDeclaredMethods();
        for (Method m : allMethods) {
            if (!m.getName().equals(methodName)) {
                continue;
            }
            out.format("%s%n", m.toGenericString());
            out.format("  Modifiers:  %s%n",
                    Modifier.toString(m.getModifiers()));
            out.format("  [ synthetic=%-5b var_args=%-5b bridge=%-5b ]%n",
                    m.isSynthetic(), m.isVarArgs(), m.isBridge());
            inc();
        }
        out.format("%d matching overload%s found%n", cnt(),
                (cnt() == 1 ? "" : "s"));

        // production code should handle this exception more gracefully
    }
    public static void methodInvokeTest(){
        try {
            Class c=Deet.class;
            Object t = c.newInstance();

            Method[] allMethods = c.getDeclaredMethods();
            for (Method m : allMethods) {
                String mname = m.getName();
                if (!mname.startsWith("test")
                        || (m.getGenericReturnType() != boolean.class)) {
                    continue;
                }
                Type[] pType = m.getGenericParameterTypes();
                if ((pType.length != 1)
                        || Locale.class.isAssignableFrom(pType[0].getClass())) {
                    continue;
                }

                out.format("invoking %s()%n", mname);
                try {
                    m.setAccessible(true);
                    Object o = m.invoke(t, new Locale("ja", "JP", "JP"));
                    out.format("%s() returned %b%n", mname, (Boolean) o);

                    // Handle any exceptions thrown by method to be invoked.
                } catch (InvocationTargetException x) {
                    Throwable cause = x.getCause();
                    err.format("invocation of %s failed: %s%n",
                            mname, cause.getMessage());
                }
            }
        } catch (InstantiationException x) {
            x.printStackTrace();
        } catch (IllegalAccessException x) {
            x.printStackTrace();
        }
    }
    /////////////////////////////////构造函数/////////////////////
    public static void constructSearchModifier(Class<?> c) {
        constructSearchModifier(c, 0);
    }

    public static void constructSearchModifier(Class<?> c, int searchMod) {
        Constructor[] allConstructors = c.getDeclaredConstructors();
        for (Constructor ctor : allConstructors) {
            int mods = accessModifiers(ctor.getModifiers());
            if (searchMod == mods) {
                out.format("%s%n", ctor.toGenericString());
                out.format("  [ synthetic=%-5b var_args=%-5b ]%n",
                        ctor.isSynthetic(), ctor.isVarArgs());
            }
        }
        // production code should handle this exception more gracefully
    }

    public static void searchField() {
        Constructor[] ctors = Console.class.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }

        try {
            //改变可见性
            ctor.setAccessible(true);
            Console c = (Console) ctor.newInstance();
            Field f = c.getClass().getDeclaredField("cs");
            f.setAccessible(true);
            out.format("Console charset         :  %s%n", f.get(c));
            out.format("Charset.defaultCharset():  %s%n",
                    Charset.defaultCharset());

            // production code should handle these exceptions more gracefully
        } catch (InstantiationException x) {
            x.printStackTrace();
        } catch (InvocationTargetException x) {
            x.printStackTrace();
        } catch (IllegalAccessException x) {
            x.printStackTrace();
        } catch (NoSuchFieldException x) {
            x.printStackTrace();
        }
    }

    public static void useConstruct() {
        Map<String, String> defaultAliases = new HashMap<String, String>();
        defaultAliases.put("Duke", "duke@i-love-java");
        defaultAliases.put("Fang", "fang@evil-jealous-twin");
        try {
            Constructor ctor = EmailAliases.class.getDeclaredConstructor(HashMap.class);
            ctor.setAccessible(true);
            EmailAliases email = (EmailAliases) ctor.newInstance(defaultAliases);
            email.printKeys();

            // production code should handle these exceptions more gracefully
        } catch (InstantiationException x) {
            x.printStackTrace();
        } catch (IllegalAccessException x) {
            x.printStackTrace();
        } catch (InvocationTargetException x) {
            x.printStackTrace();
        } catch (NoSuchMethodException x) {
            x.printStackTrace();
        }

    }

    private static int accessModifiers(int m) {
        return m & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED);
    }

    private static int modifierFromString(String s) {
        int m = 0x0;
        if ("public".equals(s)) m |= Modifier.PUBLIC;
        else if ("protected".equals(s)) m |= Modifier.PROTECTED;
        else if ("private".equals(s)) m |= Modifier.PRIVATE;
        else if ("static".equals(s)) m |= Modifier.STATIC;
        else if ("final".equals(s)) m |= Modifier.FINAL;
        else if ("transient".equals(s)) m |= Modifier.TRANSIENT;
        else if ("volatile".equals(s)) m |= Modifier.VOLATILE;
        return m;
    }

}
