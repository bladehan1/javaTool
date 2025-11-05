package Collection;

import Common.Grade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class MyListUtil {
    //按排序+去重
    //orderBy 多列
   List<?> myList =new ArrayList<>();
   //按某几列去重
   public static <T> List<T> sortList(List<T> list, Comparator<? super T> comparator, boolean distinct){
       if(distinct){
         return   list.stream().sorted().distinct().collect(Collectors.toList());
       }else {
           return list.stream().sorted().collect(Collectors.toList());
       }
   }

   //分组+排序+去重
    public static void main(String[] args){
//       List<Grade>  gradeList =new ArrayList<>();
//       Grade g11=new Grade();
//       Grade g21=new Grade();
//       Grade g31=new Grade();
//        Grade g41=new Grade();
//        Grade g13=new Grade();
//        Grade g12=new Grade();
//        gradeList.add(g11);
//        gradeList.add(g21);
//        gradeList.add(g31);
//        gradeList.add(g41);
//        gradeList.add(g13);
//        gradeList.add(g12);
//        MyListUtil.sortList();
    }
}
