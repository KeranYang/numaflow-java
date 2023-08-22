package io.numaproj.numaflow.sinker;

import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
class HandlerDatum implements Datum {

    private String[] keys;
    private byte[] value;
    private Instant watermark;
    private Instant eventTime;
    private String id;

    @Override
    public String[] getKeys() {
        return keys;
    }

    @Override
    public Instant getWatermark() {
        return this.watermark;
    }

    @Override
    public byte[] getValue() {
        return this.value;
    }

    @Override
    public Instant getEventTime() {
        return this.eventTime;
    }

    @Override
    public String getId() {
        return id;
    }
}