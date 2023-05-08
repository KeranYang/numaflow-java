package io.numaproj.numaflow.function.interfaces;

import java.time.Instant;

/**
 * Datum contains methods to get the payload information.
 */

public interface Datum {
    /**
     * method to get the payload value
     * @return returns the payload value in byte array
     */
    public byte[] getValue();

    /**
     * method to get the event time of the payload
     * @return returns the event time of the payload
     */
    public Instant getEventTime();

    /**
     * method to get the watermark information
     * @return returns the watermark
     */
    public Instant getWatermark();

    /**
     * method to get the metadata information for the Datum
     * @return returns the metadata
     */
    public DatumMetadata getDatumMetadata();
}
