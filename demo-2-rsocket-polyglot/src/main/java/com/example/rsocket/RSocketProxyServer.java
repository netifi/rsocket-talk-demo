package com.example.rsocket;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RSocketProxyServer {

    public static void main(String[] args) {
        RSocket socket = RSocketFactory.connect()
                                       //region Transport
                                       .transport(WebsocketClientTransport.create(9898))
                                       //endregion
                                       .start()
                                       .block();

        RSocketFactory.receive()
                      .acceptor((setupPayload, reactiveSocket) -> Mono.just(new AbstractRSocket() {
                          @Override
                          public Mono<Void> fireAndForget(Payload payload) {
                              return socket.fireAndForget(payload);
                          }

                          @Override
                          public Mono<Payload> requestResponse(Payload payload) {
                              return socket.requestResponse(payload);
                          }

                          @Override
                          public Flux<Payload> requestStream(Payload payload) {
                              return socket.requestStream(payload);
                          }

                          @Override
                          public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                              return socket.requestChannel(payloads);
                          }

                          @Override
                          public Mono<Void> metadataPush(Payload payload) {
                              return super.metadataPush(payload);
                          }
                      }))
                      //region Transport
                      .transport(TcpServerTransport.create("localhost", 8080))
                      //endregion
                      .start()
                      .flatMap(channel -> {
                          System.out.println("Started " + channel.address());
                          return channel.onClose();
                      })
                      .block();

    }
}
