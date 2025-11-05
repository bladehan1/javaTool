package ProxyPkg;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Collection;

public class Main {
    public static void main(String[] args){
         Hello hello=(Hello)   Proxy.newProxyInstance(Hello.class.getClassLoader(),new Class[]{Hello.class},new HelloInvocationHandler());
         hello.sayHello();
    }
    /**
     * 动态生成类的字节码,并打印生成类的构造函数，
     * 仅能生成接口的动态代理，生成类动态代理，需要新的技术
     */
    public static void getConStructors(){

        //动态生成代理类
        //ClassLoader:  每一个Class就必须有一个类加载器加载进来的，比如每个人都有一个妈妈。既然需要JVM动态生成Java类，所以要为动态生成类的字节码指定类加载器
        //Class Interfaces: 动态生成的字节码实现了哪些接口
        Class clazzProxy1 = Proxy.getProxyClass(Collection.class.getClassLoader(), Collection.class);

        //获取这个代理类的构造方法
        Constructor[] constructors = clazzProxy1.getConstructors();

        System.out.println("---------------------begin Construstors-----------------");
        //遍历构造方法
        for (Constructor constructor: constructors) {
            //获取每个名称
            String name = constructor.getName();
            StringBuilder sb = new StringBuilder(name);
            sb.append("(");
            //获取每个构造方法的参数类型
            Class[] clazzTypes = constructor.getParameterTypes();
            for (Class clazzType : clazzTypes) {
                sb.append(clazzType.getName()).append(".");
            }
            if(clazzTypes != null && clazzTypes.length != 0){
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(")");
            System.out.println(sb.toString());
        }
    }
}
