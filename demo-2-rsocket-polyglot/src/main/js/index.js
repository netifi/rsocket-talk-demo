const {RSocketServer, Utf8Encoders} = require('rsocket-core');
const {Flowable, Single} = require('rsocket-flowable');
const RSocketWebSocketServer = require('rsocket-websocket-server').default;

const server = new RSocketServer({
    getRequestHandler: socket => {
        console.log(socket);
        return new EchoResponder();
    }, transport: new RSocketWebSocketServer({
        port: 9898,
    }, Utf8Encoders)
});
server.start();

class EchoResponder {
    fireAndForget(payload) {
        console.log('fnf', payload);
    }

    requestResponse(payload) {
        console.log('requestResponse', payload);
        return Single.of({data: `Echo : ${payload.data}`, metadata: ''});
    }

    requestStream(payload) {
        console.log('requestStream', payload);
        return new Flowable(s => {
            let index = 0;
            let intervalId;

            s.onSubscribe({
                request() {
                    if (!intervalId) {
                        intervalId = setInterval(() => s.onNext(index++), 1000);
                    }
                }, cancel() {
                    clearInterval(intervalId);
                }
            });

        })
            .map(i => `Echo[${i}] : ${payload.data}`)
            .map(data => ({
                data, metadata: ''
            }));
    }

    requestChannel(payloadsFlowable) {
        console.log("channel", payloadsFlowable);
        return payloadsFlowable.map(p => ({
            data: `Echo : ${p.data}`, metadata: ''
        }))
    }

    metadataPush(payload) {
        return Single.error(new Error());
    }
}