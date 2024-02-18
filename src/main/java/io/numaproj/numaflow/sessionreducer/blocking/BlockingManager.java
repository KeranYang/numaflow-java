package io.numaproj.numaflow.sessionreducer.blocking;

import io.numaproj.numaflow.sessionreduce.v1.Sessionreduce;

import java.util.List;

// TODO - comments - also can we make this simpler?
// Unit Test it
public interface BlockingManager {
    /**
     * @param request
     */
    void enqueueRequest(Sessionreduce.SessionReduceRequest request);

    boolean isBlocking();

    void block();

    List<Sessionreduce.SessionReduceRequest> unblock();
}
