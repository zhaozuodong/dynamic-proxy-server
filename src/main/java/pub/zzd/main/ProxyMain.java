package pub.zzd.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyMain {
    static Integer prot = 8988;

    public static void main(String[] args) {
        try {
            // 在端口上创建一个Socket服务
            ServerSocket serverSocket = new ServerSocket(prot);
            while (true){
                Socket localSocket = serverSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
