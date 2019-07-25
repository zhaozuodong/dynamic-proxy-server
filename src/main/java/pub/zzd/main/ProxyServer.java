package pub.zzd.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pub.zzd.task.ProxyThread;

import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * @Description: Web动态代理服务端入口
 * @Author     : ZZD
 * @DateTime   : 2019年07月25日 上午10:14:44
 */
public class ProxyServer {
    private static Logger logger = LogManager.getLogger(ProxyServer.class);
    // 默认使用本地代理
    static Boolean isDynamicProxy = false;
    // 代理端口号
    static Integer prot = 8988;
    public static void main(String[] args) {
        // 设置是否使用动态代理 - 默认使用本地代理
        try {
            // 在端口上创建一个Socket服务
            ServerSocket serverSocket = new ServerSocket(prot);
            logger.info("WEB代理服务器正在启动...");
            logger.info("WEB代理服务器已开启，代理服务器地址：" + InetAddress.getLocalHost().getHostAddress() +":"+ prot);
            while (true){
                new ProxyThread(serverSocket.accept(),isDynamicProxy).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
