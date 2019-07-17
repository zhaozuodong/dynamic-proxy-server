package pub.zzd.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ProxyThread extends Thread {
    Socket socket;

    public ProxyThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();


        } catch (IOException e) {
            e.printStackTrace();
        }
        super.run();
    }
}