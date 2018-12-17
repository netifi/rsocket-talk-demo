# Demo 1 RSocket Java

This sample code demonstrates how we can create a simple RSocket-Java server with a 
simple Responder that can respond to *request-response*:

```java
public Mono<Payload> requestResponse(Payload payload) {
  return Mono.just(DefaultPayload.create("Echo : " + payload.getDataUtf8()));
}
```

and to *request-stream*:

```java
public Flux<Payload> requestStream(Payload payload) {
  return sharedFLux
    .map(i -> DefaultPayload.create("Echo[" + i + "] : " + payload.getDataUtf8()));
}
```

To test the given API, we can use [RSocket-CLI](https://github.com/rsocket/rsocket-cli/)
By executing the following command:

```bash
rsocket-cli --request -i 'Hello World' tcp://localhost:8080
```

we observer:

```bash
Echo : Hello World
```

By executing the following command from the CLI: 

```bash
rsocket-cli --stream -i 'Test Message' -r 5 tcp://localhost:8080
```

we observer five messages within the next 5 seconds

```bash
Echo[0] : Test Message
Echo[1] : Test Message
Echo[2] : Test Message
Echo[3] : Test Message
Echo[4] : Test Message
```