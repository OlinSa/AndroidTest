package http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class HttpThread implements Runnable {
    private final String TAG = HttpThread.class.getSimpleName();
    private Socket mSocket;
    private HttpCallback mCallback;

    public HttpThread(Socket socket, HttpCallback callback) {
        mSocket = socket;
        mCallback = callback;
    }

    @Override
    public void run() {
        String content;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            StringBuilder request = new StringBuilder();
            while (((content = reader.readLine()) != null)
                    && !content.trim().isEmpty()) {
                request.append(content).append("\n");
            }
            System.out.println("request:" + request);
            byte[] response = new byte[0];
            if (mCallback != null) {
                response = mCallback.onResponse(request.toString());
            }
            // data->http
            String responseStatus = "HTTP/1.1 200 OK \r\n";
            String responseHeader = "Content-type:"+"text/html" +"\r\n";

            OutputStream outputStream = mSocket.getOutputStream();
            outputStream.write(responseStatus.getBytes());
            outputStream.write(responseHeader.getBytes());
            outputStream.write("\r\n".getBytes());
            outputStream.write(response);
            mSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
