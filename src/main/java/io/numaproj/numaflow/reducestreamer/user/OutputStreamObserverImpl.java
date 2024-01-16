package io.numaproj.numaflow.reducestreamer.user;

import akka.actor.ActorRef;
import io.numaproj.numaflow.reducestreamer.model.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OutputStreamObserverImpl implements OutputStreamObserver {
    ActorRef reduceActor;

    @Override
    public void send(Message message) {
        this.reduceActor.tell(message, ActorRef.noSender());
        /*
        ReduceResponse response = buildResponse(message);
        responseObserver.onNext(response);
         */
    }
}
