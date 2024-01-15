package io.numaproj.numaflow.reducestreamer.metadata;

import io.numaproj.numaflow.reducestreamer.IntervalWindow;
import lombok.AllArgsConstructor;

import java.time.Instant;

/**
 * IntervalWindowImpl implements IntervalWindow interface which will be passed as metadata to reduce
 * handlers
 */
@AllArgsConstructor
public class IntervalWindowImpl implements IntervalWindow {
    private final Instant startTime;
    private final Instant endTime;

    @Override
    public Instant getStartTime() {
        return this.startTime;
    }

    @Override
    public Instant getEndTime() {
        return this.endTime;
    }
}
