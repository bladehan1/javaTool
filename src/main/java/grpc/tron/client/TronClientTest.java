package grpc.tron.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.tron.api.WalletGrpc;
import org.tron.protos.Protocol;
import com.google.protobuf.ByteString;

/**
 * TRON gRPC客户端测试
 */
public class TronClientTest {
    private final WalletGrpc.WalletBlockingStub blockingStub;

    public TronClientTest(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = WalletGrpc.newBlockingStub(channel);
    }

    public void testGetAccount() {
        System.out.println("=== 测试查询账户 ===");
        
        Protocol.Account request = Protocol.Account.newBuilder()
                .setAddress(ByteString.copyFromUtf8("TXYZ1234567890abcdef"))
                .build();
        
        Protocol.Account response = blockingStub.getAccount(request);
        System.out.println("账户余额: " + response.getBalance());
        System.out.println("账户类型: " + response.getType());
    }

    public static void main(String[] args) {
        TronClientTest client = new TronClientTest("localhost", 50051);
        client.testGetAccount();
    }
}