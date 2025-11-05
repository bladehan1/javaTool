package concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class Sum {

  public static void main(String[] args) {
    // 并发计算1-100的和，打印计算的次数
    int threadNum = Runtime.getRuntime().availableProcessors();
    //1. 线程工厂
    ThreadFactory tFactory = new ThreadFactoryBuilder().setNameFormat("sum" + "-%d").setDaemon(false).build();
    //2. 并发执行器设置
    ExecutorService executorService = Executors.newFixedThreadPool(threadNum, tFactory);
    Map<Integer, Future<Integer>> map = new HashMap<>();
    int stepLen = 100 / threadNum;
    for (int i = 0; i < threadNum; i++) {
      int start = 1 + i * stepLen;
      int end = start + stepLen - 1;
      if (i == threadNum - 1) {
        end = 100;
      }
      // lamda 表达式中使用的参数 必须是final 或者等效final
      // 3. 并发执行器添加任务，根据结果future 设置任务返回值
      int finalEnd = end;
      Future<Integer> future = executorService.submit(() -> {
        int sum = 0;
        for (int m = start; m <= finalEnd; m++) {
          sum += m;
        }
        return sum;
      });
      map.put(i, future);
    }
    int allSum = 0;
    //4. 等待计算完成
    for (Map.Entry<Integer, Future<Integer>> entry : map.entrySet()) {
      try {
        allSum += entry.getValue().get();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      }
    }
    //5. 关闭线程池
    executorService.shutdown();
    System.out.println("计算结果为：" + allSum);

  }


}
