package pub.zzd.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pub.zzd.task.ProxyThread;
import pub.zzd.utils.HostUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: Web动态代理服务端入口
 * @Author : ZZD
 * @DateTime : 2019年07月25日 上午10:14:44
 */
public class ProxyServer {
    private static Logger logger = LogManager.getLogger(ProxyServer.class);
    // 默认使用本地代理
    static Boolean isDynamicProxy = false;
    // 代理端口号
    static Integer prot = 8988;
    static Pattern portPattern = Pattern.compile("(?<=--port=)\\d+");
    static Pattern proxyPattern = Pattern.compile("(?<=--proxy=)\\S+");
    public static void main(String[] args) {
        StringBuilder conf = new StringBuilder();
        for (String arg : args) {
            if (arg != null && arg != ""){
                conf.append(arg);
            }
        }
        if (conf != null){
            String port = getRegex(conf.toString(), portPattern);
            String proxy = getRegex(conf.toString(), proxyPattern);
            if (port != null){
                // 设置端口号 - 默认使用8988
                prot = Integer.valueOf(port);
            }
            if (proxy != null){
                // 设置是否使用动态代理 - 默认使用本地代理
                isDynamicProxy = Boolean.valueOf(proxy);
            }
        }
        try {
            // 在端口上创建一个Socket服务
            ServerSocket serverSocket = new ServerSocket(prot);
            logger.info("Web代理服务器 >> 正在启动...");
            if (isDynamicProxy){
                logger.info("Web代理服务器 >> 启动动态代理");
            }
            Thread.sleep(1000);
            logger.info("Web代理服务器 >> 服务已开启");
            logger.info("Web代理服务器 >> 服务器地址：" + HostUtils.getHostIp() + ":" + prot);
            while (true) {
                Socket socket = serverSocket.accept();
                new ProxyThread(socket, isDynamicProxy).start();
            }
        } catch (Exception e) {

        }
    }

    public static String getRegex(String content,Pattern pattern){
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()){
            return matcher.group();
        }else {
            return null;
        }
    }
}
