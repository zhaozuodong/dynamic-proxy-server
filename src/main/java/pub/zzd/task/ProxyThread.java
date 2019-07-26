package pub.zzd.task;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pub.zzd.utils.MyC3P0Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Description: 代理线程
 * @Author     : ZZD
 * @DateTime   : 2019年07月25日 上午10:14:44
 */
public class ProxyThread extends Thread {
    private static Logger logger = LogManager.getLogger(ProxyThread.class);
    public static final Integer TIME_OUT = 60000;
    public Socket socket;
    static Boolean isDynamicProxy = false;
    static String DynamicHost = "";
    static Integer DynamicPort = 80;

    public ProxyThread(Socket socket) {
        this.socket = socket;
    }

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

            }
        }
    }

    @Override
    public void run() {
        try {
            // 设置代理服务器与客户端的连接未活动超时时间
            socket.setSoTimeout(TIME_OUT);
            InputStream is = socket.getInputStream();
            if (isDynamicProxy){
                Socket proxySocket = new Socket(DynamicHost,DynamicPort);
                OutputStream proxyOs = proxySocket.getOutputStream();
                InputStream proxyIs = proxySocket.getInputStream();
                OutputStream os = socket.getOutputStream();
                new ProxyHandleThread(is, proxyOs).start();
                new ProxyHandleThread(proxyIs, os).start();
            }else {
                String line = "";
                String tempHost="",host;
                int port =80;
                String type=null;
                OutputStream os = socket.getOutputStream();
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
                if(host!=null&&!host.equals("")) {
                    Socket proxySocket = new Socket(host,port);
                    //设置超时时间
                    proxySocket.setSoTimeout(TIME_OUT);
                    OutputStream proxyOs = proxySocket.getOutputStream();
                    InputStream proxyIs = proxySocket.getInputStream();
                    if(type.equalsIgnoreCase("connect")) {
                        os.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
                        os.flush();
                    }else {
                        //http请求则直接转发
                        proxyOs.write(sb.toString().getBytes("utf-8"));
                        proxyOs.flush();
                    }
                    new ProxyHandleThread(is, proxyOs).start();
                    new ProxyHandleThread(proxyIs, os).start();
                }
            }
        }catch (Exception e){

        }
    }
}