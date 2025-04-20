package io.numaproj.numaflow.sinker;

class Constants {
  // Socket Paths
  public static final String DEFAULT_SOCKET_PATH = "/var/run/numaflow/sink.sock";
  public static final String DEFAULT_FB_SINK_SOCKET_PATH = "/var/run/numaflow/fb-sink.sock";

  // Server Info File Paths
  public static final String DEFAULT_SERVER_INFO_FILE_PATH = "/var/run/numaflow/sinker-server-info";
  public static final String DEFAULT_FB_SERVER_INFO_FILE_PATH =
      "/var/run/numaflow/fb-sinker-server-info";

  // Default gRPC configurations
  public static final int DEFAULT_MESSAGE_SIZE = 1024 * 1024 * 64;
  public static final int DEFAULT_PORT = 50051;
  public static final String DEFAULT_HOST = "localhost";

  // Environment Variable NUMAFLOW_UD_CONTAINER_TYPE is used to determine the type of container.
  // It's used by ud-sink to determine the socket path and info file path
  public static final String ENV_UD_CONTAINER_TYPE = "NUMAFLOW_UD_CONTAINER_TYPE";
  // UD_CONTAINER_FALLBACK_SINK is the value of NUMAFLOW_UD_CONTAINER_TYPE when ud-sink is running
  // as a fallback sink.
  public static final String UD_CONTAINER_FALLBACK_SINK = "fb-udsink";

  // Private constructor to prevent instantiation
  private Constants() {
    throw new IllegalStateException("Utility class");
  }
}
