package pub.zzd.main;

import pub.zzd.task.ProxyThread;

import java.net.ServerSocket;

public class ProxyMain {
    static Integer prot = 8988;
    static Boolean isProxy = false;
    public static void main(String[] args) {
        try {
            // 在端口上创建一个Socket服务
            ServerSocket serverSocket = new ServerSocket(prot);
            while (true){
                new ProxyThread(serverSocket.accept(),isProxy).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
