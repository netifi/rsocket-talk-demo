package com.example.rsocket;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.publisher.Flux;
import reactor.util.Loggers;

public class RpcClient {

    public static void main(String[] args) throws Exception {
        Loggers.useConsoleLoggers();
        Logger.getGlobal().setLevel(Level.INFO);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8888)
                                                      .usePlaintext()
                                                      .build();
        ReactorGreeterGrpc.ReactorGreeterStub stub = ReactorGreeterGrpc.newReactorStub(channel);

        //region RSocket
        /*
        RSocket rSocket = RSocketFactory
                .connect()
                // region Transport
                .transport(TcpClientTransport.create(8888))
                // endregion
                .start()
                .block();

        GreeterClient stub = new GreeterClient(rSocket);
        */
        //endregion


        /*
         * Call an async BI-DIRECTIONAL STREAMING operation
         */
        Flux
            // Call service
            .<String>create(s -> {
                AtomicLong atomicLong = new AtomicLong();
                s.onRequest(l -> {
                        System.out.println("Server requested : " + l);
                        System.out.println("Server total requested : " + atomicLong.get());
                    for (int i = 0; i < l; i++) {
                        String e = String.valueOf(atomicLong.getAndIncrement());
                        s.next(e);
                    }
                });
            })
            .map(name -> HelloRequest.newBuilder()
                                     .setName(name)
                                     .build())
            .compose(stub::streamGreet)
            // Map response
            .map(HelloResponse::getMessage)
            .log()
            .subscribe(
                e -> {},
                t -> {},
                () -> {},
                subscription -> subscription.request(1)
            );

        Thread.sleep(Duration.ofSeconds(10000)
                             .toMillis());
    }
}
