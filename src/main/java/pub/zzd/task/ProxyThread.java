package pub.zzd.task;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
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
        Socket outbound = null;
        try {
            String method="";
            String host="";
            Integer port = 80;
            InputStream ips = socket.getInputStream();
            OutputStream os=null;
            boolean space;
            int state=0;
            while (true){
                int read = ips.read();

                if (read==-1){
                    break;
                }
                // 判断指定字符是否为空白字符，空白符包含：空格、tab 键、换行符。
                space=Character.isWhitespace((char)read);
                switch (state){
                    case 0 :
                        if (space){
                            continue;
                        }else {
                            state = 1;
                        }
                    case 1 :
                        if (space) {
                            state=2;
                            continue;
                        }
                        method=method+(char)read;
                        break;
                    case 2:
                        // 跳过多个空白字符
                        if (space) {
                            continue;
                        }else {
                            state=3;
                        }
                    case 3:
                        if (!space) {
                            host=host+(char)read;
                            break;
                        }else {
                            state=4;
                            // 只取出主机名称部分
                            String host0=host;
                            int n;
                            n=host.indexOf("//");
                            if (n > -1){
                                host=host.substring(n+2);
                            }
                            n=host.indexOf('/');
                            if (n > -1){
                                host=host.substring(0,n);
                            }
                            // 分析可能存在的端口号
                            n=host.indexOf(":");
                            if (n > -1) {
                                port=Integer.parseInt(host.substring(n+1));
                                host=host.substring(0,n);
                            }
                            System.out.println(method + "  " +host + "  " +port);
                            int retry = 3;
                            while (retry-- != 0) {
                                try {
                                    outbound = new Socket(host,port);
                                    break;
                                } catch (Exception e) { }
                                // 等待
                                Thread.sleep(2*1000);
                            }
                            if (outbound==null){
                                System.out.println("================================");
                                break;
                            }
                            outbound.setSoTimeout(30*1000);
                            os=outbound.getOutputStream();
                            os.write(method.getBytes());
                            os.write(' ');
                            os.write(host0.getBytes());
                            os.write(' ');
                            InputStream inputStream = outbound.getInputStream();
                            OutputStream outputStream = socket.getOutputStream();
                            pipe(ips,inputStream,os,outputStream);
                            break;
                        }
                    default:break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (Exception e1) {

            }
            try {
                outbound.close();
            } catch (Exception e2) {

            }
        }
        super.run();
    }

    //           客户端输入       服务端输入
    void pipe(InputStream is0, InputStream is1, OutputStream os0,  OutputStream os1) throws Exception {
        try {
            int ir;
            byte bytes[]=new byte[1024];
            while (true) {
                try {
                    if ((ir=is0.read(bytes))>0) {
                        os0.write(bytes,0,ir);
                    }
                    else if (ir < 0){
                        break;
                    }
                } catch (InterruptedIOException e) {

                }
                try {
                    if ((ir=is1.read(bytes))>0) {
                        os1.write(bytes,0,ir);
                    }
                    else if (ir < 0){
                        break;
                    }
                } catch (InterruptedIOException e) {

                }
            }
        } catch (Exception e0) {

        }
        is0.close();
        is1.close();
        os0.close();
        os1.close();
    }
}