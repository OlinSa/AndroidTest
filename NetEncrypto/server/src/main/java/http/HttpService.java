package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpService {
    private boolean mRunning = false;
    private HttpCallback mCallback;

    public HttpService(HttpCallback callback) {
        mCallback = callback;
    }

    public void startHttpServer() {
        if (mRunning) {
            return;
        }
        mRunning = true;
        try {
            ServerSocket serversocket = new ServerSocket(8000);
            while (mRunning) {
                Socket socket = serversocket.accept();
                ExecutorService threadPool = Executors.newCachedThreadPool();
                threadPool.execute(new HttpThread(socket, mCallback));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
