package io.numaproj.numaflow.reducestreamer;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import io.numaproj.numaflow.reduce.v1.ReduceOuterClass;
import io.numaproj.numaflow.reducestreamer.model.Message;
import io.numaproj.numaflow.reducestreamer.model.Metadata;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ResponseStreamActor is dedicated to ensure synchronized calls to the responseObserver onNext().
 * ALL the response messages are sent to ResponseStreamActor before getting sent to output gRPC stream.
 */
@Slf4j
@AllArgsConstructor
public class ResponseStreamActor extends AbstractActor {
    StreamObserver<ReduceOuterClass.ReduceResponse> responseObserver;
    Metadata md;

    public static Props props(
            StreamObserver<ReduceOuterClass.ReduceResponse> responseObserver,
            Metadata md) {
        return Props.create(ResponseStreamActor.class, responseObserver, md);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, this::sendMessage)
                .match(ActorEOFResponse.class, this::sendEOF)
                .build();
    }

    private void sendMessage(Message message) {
        // Synchronized access to the output stream
        synchronized (responseObserver) {
            responseObserver.onNext(this.buildResponse(message));
        }
    }

    private void sendEOF(ActorEOFResponse actorEOFResponse) {
        // Synchronized access to the output stream
        synchronized (responseObserver) {
            responseObserver.onNext(actorEOFResponse.getResponse());
        }
        System.out.println("kerantest" + getSender().toString());
        getSender().tell(
                new EofResponseSentSignal(actorEOFResponse.getResponse()),
                getSelf());
    }

    private ReduceOuterClass.ReduceResponse buildResponse(Message message) {
        ReduceOuterClass.ReduceResponse.Builder responseBuilder = ReduceOuterClass.ReduceResponse.newBuilder();
        // set the window using the actor metadata.
        responseBuilder.setWindow(ReduceOuterClass.Window.newBuilder()
                .setStart(Timestamp.newBuilder()
                        .setSeconds(this.md.getIntervalWindow().getStartTime().getEpochSecond())
                        .setNanos(this.md.getIntervalWindow().getStartTime().getNano()))
                .setEnd(Timestamp.newBuilder()
                        .setSeconds(this.md.getIntervalWindow().getEndTime().getEpochSecond())
                        .setNanos(this.md.getIntervalWindow().getEndTime().getNano()))
                .setSlot("slot-0").build());
        responseBuilder.setEOF(false);
        // set the result.
        responseBuilder.setResult(ReduceOuterClass.ReduceResponse.Result
                .newBuilder()
                .setValue(ByteString.copyFrom(message.getValue()))
                .addAllKeys(message.getKeys()
                        == null ? new ArrayList<>():Arrays.asList(message.getKeys()))
                .addAllTags(
                        message.getTags() == null ? new ArrayList<>():List.of(message.getTags()))
                .build());

        return responseBuilder.build();
    }
}
