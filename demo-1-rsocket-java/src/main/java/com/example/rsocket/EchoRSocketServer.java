/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.rsocket;

import java.time.Duration;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class EchoRSocketServer {

    static Flux<Long> sharedFLux =  Flux.interval(Duration.ofSeconds(1))
                                        .share();

    public static void main(String[] args) {
        RSocketFactory.receive()
                      .acceptor((p, rsocket) -> Mono.just(new AbstractRSocket() {
                          @Override
                          public Mono<Payload> requestResponse(Payload payload) {
                              return Mono.just(DefaultPayload.create("Echo : " + payload.getDataUtf8()));
                          }

                          @Override
                          public Flux<Payload> requestStream(Payload payload) {
                              return sharedFLux
                                  .map(i -> DefaultPayload.create("Echo[" + i + "] : " + payload.getDataUtf8()));
                          }
                      }))
                      //region Transport setup
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
