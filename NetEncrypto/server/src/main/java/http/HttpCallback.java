package http;

public interface HttpCallback {
    byte[] onResponse(String request);
}
