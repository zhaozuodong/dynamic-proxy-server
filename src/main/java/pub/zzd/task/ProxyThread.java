package pub.zzd.task;

import java.io.InputStream;
import java.net.Socket;

public class ProxyThread extends Thread {
    public Socket socket;
    public Boolean isProxy;

    public ProxyThread(Socket socket,Boolean isProxy) {
        this.socket = socket;
        this.isProxy = isProxy;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            // 1、先获取请求内容



            if (isProxy){
                // 走第三方代理
                Socket socket = new Socket("", 123);

            }else {
                // 使用本地代理
            }

            // 完成后关闭资源
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.run();
    }
}