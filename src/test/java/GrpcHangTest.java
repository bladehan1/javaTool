import com.google.protobuf.ByteString;
import grpc.tron.server.AdvancedTronServer;
import io.grpc.*;
import java.io.FileWriter;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.tron.api.WalletGrpc;
import org.tron.protos.Protocol;


import static org.junit.Assert.assertNotNull;

public class GrpcHangTest {

  private AdvancedTronServer server;
  private ManagedChannel channel;
  private final int port = 50053;

  // 自动记录日志的 TestWatcher
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


    // Channel: 不指定 executor（使用默认），这是常见会触发 ThreadlessExecutor 的场景
    channel = ManagedChannelBuilder.forAddress("localhost", port)
        .usePlaintext()
        .build();
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

  // 即便该测试是顺序执行、只有一个 RPC 请求，gRPC 内部依然会多线程调度
 // 导致 ThreadlessExecutor + Netty EventLoop 之间的死锁更容易出现
  @Test(timeout = 60000) // JUnit 的 30s watchdog（如你描述）— 如果 gRPC 内部 deadlock 会在这被触发
  public void reportBlockingUnaryHang() {
    // 构造 blocking stub（默认）


    Protocol.Account request = Protocol.Account.newBuilder()
        .setAddress(ByteString.copyFromUtf8("TXYZ1234567890abcdef"))
        .build();

    for (int i = 0; i < 200; i++) {
      WalletGrpc.WalletBlockingStub blockingStub = WalletGrpc.newBlockingStub(channel);
      Protocol.Account response = blockingStub.getAccount(request);
      assertNotNull(response);
    }
  }
}

