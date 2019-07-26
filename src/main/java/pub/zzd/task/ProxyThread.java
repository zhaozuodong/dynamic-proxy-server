package pub.zzd.task;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pub.zzd.utils.MyC3P0Utils;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 代理线程
 * @Author     : ZZD
 * @DateTime   : 2019年07月25日 上午10:14:44
 */
public class ProxyThread extends Thread {
    private static Logger logger = LogManager.getLogger(ProxyThread.class);
    public static final Integer TIME_OUT = 30000;
    public Socket socket;
    static Boolean isDynamicProxy = false;
    static String DynamicHost = "";
    static Integer DynamicPort = 80;
    static ThreadPoolExecutor pool = new ThreadPoolExecutor(60,60,30, TimeUnit.SECONDS,new ArrayBlockingQueue(120));

    public ProxyThread(Socket socket, Boolean isDynamicProxy) {
        this.socket = socket;
        this.isDynamicProxy = isDynamicProxy;
        if (isDynamicProxy){
            try {
                QueryRunner qr = new QueryRunner(MyC3P0Utils.getDataSource());
                String sql = "select host,port from t_proxy order by rand() limit 1";
                Object[] query = qr.query(sql, new ArrayHandler());
                this.DynamicHost = query[0].toString();
                this.DynamicPort = Integer.valueOf(query[1].toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (isDynamicProxy){
            try {
                // 获取客户端请求
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                Socket proxySocket = new Socket(DynamicHost,DynamicPort);
                // 设置与代理服务器的超时时间
                proxySocket.setSoTimeout(TIME_OUT);
                OutputStream proxyOs = proxySocket.getOutputStream();
                InputStream proxyIs = proxySocket.getInputStream();
                Future<?> client = pool.submit(new ProxyHandleThread(is, proxyOs));
                Future<?> server = pool.submit(new ProxyHandleThread(proxyIs, os));
                client.get();
                server.get();
                proxySocket.close();
                socket.close();
//                new ProxyHandleThread(is, proxyOs).start();
//                new ProxyHandleThread(proxyIs, os).start();
            }catch (ConnectException e){
                System.out.println("代理连接异常:" + e);
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                String line = "";
                String tempHost="",host;
                int port =80;
                String type=null;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                int temp=1;
                StringBuilder sb =new StringBuilder();
                while((line = br.readLine())!=null) {
                    if(temp==1) {
                        type = line.split(" ")[0];
                        if(type==null){
                            continue;
                        }
                    }
                    temp++;
                    String[] s1 = line.split(": ");
                    if(line.isEmpty()) {
                        break;
                    }
                    for(int i=0;i<s1.length;i++) {
                        if(s1[i].equalsIgnoreCase("host")) {
                            tempHost=s1[i+1];
                        }
                    }
                    sb.append(line + "\r\n");
                    line = null;
                }
                sb.append("\r\n");
                if(tempHost.split(":").length>1) {
                    port = Integer.valueOf(tempHost.split(":")[1]);
                }
                host = tempHost.split(":")[0];
                if(StringUtils.isNotBlank(host)) {
                    Socket proxySocket = new Socket(host,port);
                    // 设置与目标服务器的超时时间
                    proxySocket.setSoTimeout(TIME_OUT);
                    OutputStream proxyOs = proxySocket.getOutputStream();
                    InputStream proxyIs = proxySocket.getInputStream();
                    if(type.equalsIgnoreCase("connect")) {
                        os.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
                        os.flush();
                    }else {
                        // HTTP的请求直接转发
                        proxyOs.write(sb.toString().getBytes("utf-8"));
                        proxyOs.flush();
                    }
                    Future<?> client = pool.submit(new ProxyHandleThread(is, proxyOs));
                    Future<?> server = pool.submit(new ProxyHandleThread(proxyIs, os));
                    client.get();
                    server.get();
                    proxySocket.close();
                    socket.close();
//                    new ProxyHandleThread(is, proxyOs).start();
//                    new ProxyHandleThread(proxyIs, os).start();
                }
            }catch (ConnectException e){
                System.out.println("本地连接异常:" + e);
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}