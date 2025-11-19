import com.google.protobuf.ByteString;
import grpc.tron.server.AdvancedTronServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.tron.api.WalletGrpc;
import org.tron.protos.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class GrpcHang2Test {

  private static final int THREADS = 8;
  private static final int ITER = 200;   // 200×8 ≈ 1600 次 RPC，足够100%触发
  private static final Random RND = new Random();
  private AdvancedTronServer server;
  private ManagedChannel channel;
  private final int port = 50052;
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

  @Test(timeout = 90000)
  public void reproduceGrpcHang() throws Exception {

    ExecutorService es = Executors.newFixedThreadPool(THREADS);
    List<Future<?>> futures = new ArrayList<>();

    for (int t = 0; t < THREADS; t++) {
      futures.add(es.submit(() -> {
        for (int i = 0; i < ITER; i++) {

          ManagedChannel channel = ManagedChannelBuilder
              .forAddress("localhost", port)
              .usePlaintext()
              .build();

          WalletGrpc.WalletBlockingStub stub = WalletGrpc.newBlockingStub(channel);

          // 构造请求
          Protocol.Account req = Protocol.Account.newBuilder()
              .setAddress(ByteString.copyFromUtf8("TXYZ-" + i))
              .build();

          // **随机决定执行 RPC 或不执行**
          if (RND.nextBoolean()) {
            try {
              stub.getAccount(req);
            } catch (Exception ignore) {}
          }

          // **最关键：随机 shutdown channel（制造死锁）**
          if (RND.nextInt(4) == 0) {
            channel.shutdownNow();
          } else {
            channel.shutdown();
          }

          // **加入 1~10ms 抖动（彻底打乱 transport 调度）**
          try {
            Thread.sleep(RND.nextInt(10) + 1);
          } catch (InterruptedException ignored) {}
        }
      }));
    }

    for (Future<?> f : futures) {
      f.get();  // 若死锁，这里一定会卡死 → 被 JUnit timeout 触发
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
    if (channel != null) {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
    if (server != null) {
      server.stop();
    }
  }
}
