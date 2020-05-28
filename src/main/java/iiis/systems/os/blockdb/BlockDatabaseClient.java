package iiis.systems.os.blockdb;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class BlockDatabaseClient {
    private static final Logger log = Logger.getLogger(BlockDatabaseClient.class.getName());

    private final ManagedChannel channel;
    //阻塞/同步 的stub(存根)
    private final BlockDatabaseGrpc.BlockDatabaseBlockingStub blockingStub;

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) {
        BlockDatabaseClient client = new BlockDatabaseClient("127.0.0.1", 50051);
        try {
            if (args.length > 0) {
                switch (args[0]) {
                    case "get" :
                        if (args.length != 2) System.out.println("Invalid arguments") ;
                        client.get(args[1]) ;
                        break ;
                    case "put" :
                        if (args.length != 3) System.out.println("Invalid arguments") ;
                        client.put(args[1], Integer.parseInt(args[2])) ;
                        break ;
                    case "deposit" :

                        if (args.length != 3) System.out.println("Invalid arguments") ;
                        client.deposit(args[1], Integer.parseInt(args[2])) ;
                        break ;
                    case "withdraw" :
                        if (args.length != 3) System.out.println("Invalid arguments") ;
                        client.withdraw(args[1], Integer.parseInt(args[2])) ;
                        break ;
                    case "transfer" :

                        if (args.length != 4) System.out.println("Invalid arguments") ;
                        client.transfer(args[1], args[2], Integer.parseInt(args[3])) ;
                        break ;
                    case "logLength" :

                        if (args.length != 1) System.out.println("Invalid arguments") ;
                        client.logLength();
                        break ;

                }

            }
            client.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BlockDatabaseClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build());
    }

    public BlockDatabaseClient(ManagedChannel channel) {
        this.channel = channel;

        blockingStub = BlockDatabaseGrpc.newBlockingStub(   channel);
    }

    public void get(String userID) {
        log.info("Will try to get " + userID + " balance ..");
        GetRequest request = GetRequest.newBuilder().setUserID(userID).build();
        GetResponse response = null;
        try {
            //使用阻塞 stub调用
            response = blockingStub.get(request);
        } catch (StatusRuntimeException e) {
            log.info(String.format("rpc failed:%s", e.getStatus()));
            return ;
        }
        log.info("Get: " + response.getValue());
    }

    public void put(String userID, int value) {

        log.info("Will try to put " + userID + " balance .. to "+ value);
        Request request = Request.newBuilder().setUserID(userID).setValue(value).build() ;
        BooleanResponse response = null;
        try {
            //使用阻塞 stub调用
            response = blockingStub.put(request) ;
        } catch (StatusRuntimeException e) {
            log.info(String.format("rpc failed:%s", e.getStatus()));
            return ;
        }
        if (response.getSuccess()) log.info("Put Success" );
        else log.info("Put Fail, server internal error") ;
    }


    public void deposit(String userID, int value) {

        log.info("Will try to deposit " + userID + " balance .. with value "+ value);
        Request request = Request.newBuilder().setUserID(userID).setValue(value).build() ;
        BooleanResponse response = null;
        try {
            //使用阻塞 stub调用
            response = blockingStub.deposit(request) ;
        } catch (StatusRuntimeException e) {
            log.info(String.format("rpc failed:%s", e.getStatus()));
            return ;
        }
        if (response.getSuccess()) log.info("Deposit Success" );
        else log.info("Deposit Fail, server internal error") ;
    }

    public void withdraw(String userID, int value) {

        log.info("Will try to withdraw " + userID + " balance .. with value "+ value);
        Request request = Request.newBuilder().setUserID(userID).setValue(value).build() ;
        BooleanResponse response = null;
        try {
            //使用阻塞 stub调用
            response = blockingStub.withdraw(request) ;
        } catch (StatusRuntimeException e) {
            log.info(String.format("rpc failed:%s", e.getStatus()));
            return ;
        }
        if (response.getSuccess()) log.info("Withdraw Success" );
        else log.info("Withdraw Fail, maybe you spend too much money") ;
    }

    public void transfer(String fromID, String toID, int value) {

        log.info("Will try to transfer from " + fromID + " to " + toID + " with value "+ value);
        TransferRequest request = TransferRequest.newBuilder().setFromID(fromID).setToID(toID).setValue(value).build();
        BooleanResponse response = null;
        try {
            //使用阻塞 stub调用
            response = blockingStub.transfer(request) ;
        } catch (StatusRuntimeException e) {
            log.info(String.format("rpc failed:%s", e.getStatus()));
            return ;
        }
        if (response.getSuccess()) log.info("Transfer Success" );
        else log.info("Transfer Fail, maybe you spend too much money") ;
    }


    public void logLength() {
        log.info("Will try to get log length");
        Null request = Null.newBuilder().build() ;
        GetResponse response = null;
        try {
            //使用阻塞 stub调用
            response = blockingStub.logLength(request) ;
        } catch (StatusRuntimeException e) {
            log.info(String.format("rpc failed:%s", e.getStatus()));
            return ;
        }
        log.info("log length = " + response.getValue()) ;
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}