package io.numaproj.numaflow.reducestreamer;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.numaproj.numaflow.reduce.v1.ReduceOuterClass;
import io.numaproj.numaflow.reducestreamer.model.HandlerDatum;
import io.numaproj.numaflow.reducestreamer.model.Message;
import io.numaproj.numaflow.reducestreamer.model.Metadata;
import io.numaproj.numaflow.reducestreamer.user.OutputStreamObserver;
import io.numaproj.numaflow.reducestreamer.user.ReduceStreamer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reduce stream actor invokes the reducer and returns the result.
 */

@Slf4j
@AllArgsConstructor
public class ReduceStreamerActor extends AbstractActor {
    private String[] keys;
    private Metadata md;
    private ReduceStreamer groupBy;
    private OutputStreamObserver outputStreamObserver;

    public static Props props(
            String[] keys, Metadata md, ReduceStreamer groupBy) {
        return Props.create(
                ReduceStreamerActor.class,
                keys,
                md,
                groupBy);
    }

    // Setter for the output stream observer
    public void setOutputStreamObserver(OutputStreamObserver outputStreamObserver) {
        this.outputStreamObserver = outputStreamObserver;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                // Receive an input datum from input stream, invoke the udf to handle the data.
                .match(HandlerDatum.class, this::invokeHandler)
                // Receive an EOF signal, send back to the supervisor to indicate job done.
                .match(String.class, this::sendEOF)
                // Receive a message from the udf, construct actor response and send to the supervisor.
                .match(Message.class, this::sendResponse)
                .build();
    }

    private void invokeHandler(HandlerDatum handlerDatum) {
        this.groupBy.processMessage(keys, handlerDatum, outputStreamObserver, md);
    }

    private void sendEOF(String EOF) {
        getSender().tell(buildEOFResponse(), getSelf());
    }

    private void sendResponse(Message message) {
        getSender().tell(buildResponse(message), getSelf());
    }

    private ActorResponse buildResponse(Message message) {
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
        return new ActorResponse(responseBuilder.build());
    }

    private ActorResponse buildEOFResponse() {
        ReduceOuterClass.ReduceResponse.Builder responseBuilder = ReduceOuterClass.ReduceResponse.newBuilder();
        responseBuilder.setWindow(ReduceOuterClass.Window.newBuilder()
                .setStart(Timestamp.newBuilder()
                        .setSeconds(this.md.getIntervalWindow().getStartTime().getEpochSecond())
                        .setNanos(this.md.getIntervalWindow().getStartTime().getNano()))
                .setEnd(Timestamp.newBuilder()
                        .setSeconds(this.md.getIntervalWindow().getEndTime().getEpochSecond())
                        .setNanos(this.md.getIntervalWindow().getEndTime().getNano()))
                .setSlot("slot-0").build());
        responseBuilder.setEOF(true);
        // set a dummy result with the keys.
        responseBuilder.setResult(ReduceOuterClass.ReduceResponse.Result
                .newBuilder()
                .addAllKeys(List.of(this.keys))
                .build());
        return new ActorResponse(responseBuilder.build());
    }
}
