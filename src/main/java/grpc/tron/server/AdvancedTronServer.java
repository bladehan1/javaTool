package grpc.tron.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.tron.api.GrpcAPI;
import org.tron.api.WalletGrpc;
import org.tron.protos.Protocol;
import org.tron.protos.contract.BalanceContract;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * 高级TRON服务端实现，包含模拟数据库
 */
public class AdvancedTronServer {
    private static final Logger logger = Logger.getLogger(AdvancedTronServer.class.getName());
    //使用固定系统核数线程，增加复现概率
    private final ExecutorService serverExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final int port;
    public  Server server;
    private final Map<String, AccountData> accountDatabase;

    public AdvancedTronServer(){
      this.port = 80;
      this.accountDatabase = new ConcurrentHashMap<>();
      initializeSampleData();

      this.server = ServerBuilder.forPort(port)
          .addService(new AdvancedWalletServiceImpl())
          .build();
    }

    public AdvancedTronServer(int port) {
        this.port = port;
        this.accountDatabase = new ConcurrentHashMap<>();
        initializeSampleData();
        
        this.server = ServerBuilder.forPort(port)
                .addService(new AdvancedWalletServiceImpl())
                .executor(serverExecutor)
                .build();
    }
  public AdvancedTronServer(int port,boolean fix) {
    this.port = port;
    this.accountDatabase = new ConcurrentHashMap<>();
    initializeSampleData();
    if (fix) {
      this.server = ServerBuilder.forPort(port)
          .addService(new AdvancedWalletServiceImpl())
          .executor(Executors.newFixedThreadPool(8))
          .build();
    } else {
      this.server = ServerBuilder.forPort(port)
          .addService(new AdvancedWalletServiceImpl())
          .build();
    }
  }

    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        // 添加示例账户
        accountDatabase.put("TXYZ1234567890abcdef", 
            new AccountData(100000000L, Protocol.AccountType.Normal));
        accountDatabase.put("TXYZabcdef1234567890", 
            new AccountData(50000000L, Protocol.AccountType.Normal));
    }

    /**
     * 账户数据类
     */
    static class AccountData {
        long balance;
        Protocol.AccountType accountType;

        AccountData(long balance, Protocol.AccountType accountType) {
            this.balance = balance;
            this.accountType = accountType;
        }
    }

    /**
     * 高级Wallet服务实现
     */
    public class AdvancedWalletServiceImpl extends WalletGrpc.WalletImplBase {
        
        @Override
        public void getAccount(Protocol.Account request, 
                             StreamObserver<Protocol.Account> responseObserver) {
            String address = request.getAddress().toStringUtf8();
            logger.info("查询账户: " + address);
            
            AccountData accountData = accountDatabase.get(address);
            Protocol.Account.Builder accountBuilder = Protocol.Account.newBuilder()
                    .setAddress(request.getAddress());
            
            if (accountData != null) {
                accountBuilder.setBalance(accountData.balance)
                             .setType(accountData.accountType);
            } else {
                accountBuilder.setBalance(0L)
                             .setType(Protocol.AccountType.Normal);
                logger.warning("账户不存在: " + address);
            }
          try {
            Random random = new Random();
            int sleepTime = 20 + random.nextInt(30); //
            // 关键：强制卡住 callback → 触发 ThreadlessExecutor 死锁
            Thread.sleep(sleepTime); // 20–50ms 是最佳窗口
          } catch (InterruptedException ignored) {

          }

            responseObserver.onNext(accountBuilder.build());
            responseObserver.onCompleted();
        }

        @Override
        public void createTransaction(BalanceContract.TransferContract request,
                                    StreamObserver<Protocol.Transaction> responseObserver) {
            String fromAddress = request.getOwnerAddress().toStringUtf8();
            String toAddress = request.getToAddress().toStringUtf8();
            long amount = request.getAmount();
            
            logger.info(String.format("创建交易: %s -> %s, 金额: %d", 
                fromAddress, toAddress, amount));
            
            // 检查余额
            AccountData fromAccount = accountDatabase.get(fromAddress);
            if (fromAccount == null || fromAccount.balance < amount) {
                logger.warning("余额不足或账户不存在");
                responseObserver.onError(new RuntimeException("余额不足"));
                return;
            }
            
            // 创建交易
            Protocol.Transaction.raw rawData = Protocol.Transaction.raw.newBuilder()
                    .setTimestamp(System.currentTimeMillis())
                    .build();
            
            Protocol.Transaction transaction = Protocol.Transaction.newBuilder()
                    .setRawData(rawData)
                    .build();
            
            responseObserver.onNext(transaction);
            responseObserver.onCompleted();
        }

        @Override
        public void broadcastTransaction(Protocol.Transaction request,
                                      StreamObserver<GrpcAPI.Return> responseObserver) {
            logger.info("广播交易");
            
            // 模拟交易处理逻辑
            boolean success = processTransaction(request);
            
            GrpcAPI.Return response = GrpcAPI.Return.newBuilder()
                    .setResult(success)
                    .setCode(success ? GrpcAPI.Return.response_code.SUCCESS : 
                                     GrpcAPI.Return.response_code.SIGERROR)

                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        /**
         * 处理交易逻辑
         */
        private boolean processTransaction(Protocol.Transaction transaction) {
            // 这里可以实现实际的交易处理逻辑
            // 包括签名验证、余额检查、状态更新等
            return true; // 模拟成功
        }
    }

    public void start() throws IOException {
        server.start();
        logger.info("高级TRON服务端启动，端口: " + port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** 关闭gRPC服务端");
            try {
                AdvancedTronServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AdvancedTronServer server = new AdvancedTronServer(50051);
        server.start();
        server.blockUntilShutdown();
    }
}