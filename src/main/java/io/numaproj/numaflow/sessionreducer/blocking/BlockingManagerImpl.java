package io.numaproj.numaflow.sessionreducer.blocking;

import io.numaproj.numaflow.sessionreduce.v1.Sessionreduce;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// TODO - comments - unit test it
public class BlockingManagerImpl implements BlockingManager {
    private Queue<Sessionreduce.SessionReduceRequest> pendingRequests;
    private boolean isBlocking;

    public BlockingManagerImpl() {
        this.pendingRequests = new LinkedList<>();
        this.isBlocking = false;
    }

    @Override
    public void enqueueRequest(Sessionreduce.SessionReduceRequest request) {
        if (!this.isBlocking) {
            throw new RuntimeException(
                    "cannot enqueue a request when the blocking manager is not blocking.");
        }
        this.pendingRequests.add(request);
    }

    @Override
    public boolean isBlocking() {
        return this.isBlocking;
    }

    @Override
    public void block() {
        this.isBlocking = true;
    }

    @Override
    public List<Sessionreduce.SessionReduceRequest> unblock() {
        List<Sessionreduce.SessionReduceRequest> res = new ArrayList<>();
        Sessionreduce.SessionReduceRequest eof = null;
        while (!this.pendingRequests.isEmpty()) {
            Sessionreduce.SessionReduceRequest request = this.pendingRequests.poll();
            if (request.getPayload().getValue().toStringUtf8().equals("EOF Signal")) {
                eof = request;
            } else {
                res.add(request);
            }
        }
        if (eof != null) {
            // always put the EOF at the last.
            res.add(eof);
        }
        this.isBlocking = false;
        return res;
    }
}
