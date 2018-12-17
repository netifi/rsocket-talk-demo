package com.example.rsocket;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.netty.buffer.ByteBuf;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Loggers;

/**
 * This server implements a unary operation, a streaming response operation, and a bi-directional streaming operation.
 */
public class RpcServer extends ReactorGreeterGrpc.GreeterImplBase {
    //region RSocket Server
//public class RpcServer implements Greeter {
    //endregion

    public static void main(String[] args) throws Exception {
        Loggers.useConsoleLoggers();
        Logger.getGlobal().setLevel(Level.INFO);
        // Start the server
        ServerBuilder
            .forPort(8888)
            .addService(new RpcServer())
            .build()
            .start()
            .awaitTermination();
        //region RSocket Server Start
        /*
        RSocketFactory
            .receive()
            .acceptor((p, r) -> Mono.just(
                new GreeterServer(new RpcServer(), Optional.empty(), Optional.empty())
            ))
            //region RSocket Transport
            .transport(TcpServerTransport.create(8888))
            //endregion
            .start()
            .block()
            .onClose()
            .block();
        */
        //endregion
    }

    /**
     * Implement a BI-DIRECTIONAL STREAMING operation
     */
    @Override
    public Flux<HelloResponse> streamGreet(Flux<HelloRequest> request) {
        // region RSocket Method
//    @Override
//    public Flux<HelloResponse> streamGreet(Publisher<HelloRequest> messages, ByteBuf metadata) {
//        return Flux
//                .from(messages)
        //endregion
        return request
                .log()
                .map(HelloRequest::getName)
                .map(name -> "Greetings " + name)
                .map(greeting -> HelloResponse.newBuilder()
                                              .setMessage(greeting)
                                              .build());
    }
}
