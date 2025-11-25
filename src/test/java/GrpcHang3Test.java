import com.google.protobuf.ByteString;
import grpc.interceptor.DebugInterceptor;
import grpc.tron.server.AdvancedTronServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.tron.api.WalletGrpc;
import org.tron.protos.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcHang3Test {

  private static final int THREADS = 32; //大于服务端线程数
  private static final int ITER = 100;
  private static final Random RND = new Random();
  private AdvancedTronServer server;
  private final int port = 50052;

  static {
    LogManager.getLogManager().reset();
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
    Logger.getLogger("").setLevel(Level.ALL); // Root logger.
  }


  @Rule
  public TestWatcher hangWatcher = new TestWatcher() {
    final String fileStr = "hang-report.log";
    @Override
    protected void failed(Throwable e, Description description) {
      boolean isTimeout = e instanceof org.junit.runners.model.TestTimedOutException;
      try (FileWriter fw = new FileWriter(fileStr, true)) {
        fw.write("[" + LocalDateTime.now() + "] "
            + description.getMethodName()
            + " => FAILED " + (isTimeout ? "(HANG/TIMEOUT)" : "")
            + " | Exception: " + e + "\n");
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }

    @Override
    protected void succeeded(Description description) {
      try (FileWriter fw = new FileWriter(fileStr, true)) {
        fw.write("[" + LocalDateTime.now() + "] "
            + description.getMethodName()
            + " => SUCCESS\n");
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  };
//  保持hang 状态
  @Test
  public void reproduceGrpcHang() throws Exception {
    log.info("hello GrpcHang3Test reproduceGrpcHang");

    ExecutorService es = Executors.newFixedThreadPool(THREADS);
    List<Future<?>> futures = new ArrayList<>();

    // ARM特有的内存屏障和缓存压力
    final int MEMORY_STRESS_COUNT = 1000;
    final byte[] memoryBuffer = new byte[1024 * 1024]; // 1MB buffer

    for (int t = 0; t < THREADS; t++) {
      futures.add(es.submit(() -> {
        for (int i = 0; i < ITER; i++) {
          // === ARM内存压力：频繁内存访问打乱缓存 ===
          for (int j = 0; j < MEMORY_STRESS_COUNT; j++) {
            memoryBuffer[(i + j) % memoryBuffer.length] = (byte) (i + j);
          }

          ManagedChannel channel = ManagedChannelBuilder
              .forAddress("localhost", port)
              .usePlaintext()
              .intercept(new DebugInterceptor())
              // === 关键：禁用某些优化 ===
              .maxInboundMessageSize(64 * 1024 * 1024) // 强制大buffer
              .build();

          WalletGrpc.WalletBlockingStub stub = WalletGrpc.newBlockingStub(channel)
              .withDeadlineAfter(5, TimeUnit.SECONDS);

          Protocol.Account req = Protocol.Account.newBuilder()
              .setAddress(ByteString.copyFromUtf8("TXYZ1234567890abcde" + i))
              .build();

          // === 移除随机跳过，每次都执行RPC ===
          try {
            stub.getAccount(req);
          } catch (Exception ignore) {
            // 记录异常但不中断
          }

          // === 强化shutdown竞争 ===
          int shutdownType = RND.nextInt(4);
          switch (shutdownType) {
            case 0:
              channel.shutdown();
              try {
                channel.awaitTermination(10, TimeUnit.MILLISECONDS);
              } catch (InterruptedException e) {}
              break;
            case 1:
              channel.shutdownNow();
              break;
            case 2:
              // 双重shutdown制造更多竞争
              channel.shutdown();
              channel.shutdownNow();
              break;
            case 3:
              // 不立即shutdown，制造泄漏
              if (RND.nextInt(100) < 10) { // 10%概率泄漏
                // 不shutdown，让GC处理
              } else {
                channel.shutdownNow();
              }
              break;
          }

          // === 调整延迟策略 ===
          try {
            // 更频繁的短延迟：0-5ms
            Thread.sleep(RND.nextInt(6));
            // 偶尔的长延迟打乱时序
            if (RND.nextInt(100) < 5) { // 5%概率
              Thread.sleep(20 + RND.nextInt(30));
            }
          } catch (InterruptedException ignored) {}

          // === 强制GC增加内存压力 ===
          if (i % 50 == 0) {
            System.gc();
          }
        }
      }));
    }

    for (Future<?> f : futures) {
      f.get();
    }
  }
  @Before
  public void startServer() throws IOException {
    // 注意：故意不给 server 指定 executor（或使用默认），以尽量复现问题
    Thread t = new Thread(() -> {
      server = new AdvancedTronServer(port);
      try {
        server.start();
        server.blockUntilShutdown();
      } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    t.start();
  }

  @After
  public void tearDown() throws InterruptedException {
    if (server != null) {
      server.stop();
    }
  }
}
