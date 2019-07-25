package pub.zzd.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

/**
 * @Description: 传递消息并转发线程
 * @Author     : ZZD
 * @DateTime   : 2019年07月25日 上午10:14:44
 */
public class ProxyHandleThread extends Thread {
    private static Logger logger = LogManager.getLogger(ProxyHandleThread.class);
    private InputStream input;
    private OutputStream output;

    public ProxyHandleThread(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            while (true) {
                BufferedInputStream bis = new BufferedInputStream(input);
                byte[] buffer = new byte[1024];
                int length=-1;
                //这里最好是字节转发，不要用上面的InputStreamReader，因为https传递的都是密文，那样会乱码，消息传到服务器端也会出错。
                while((length=bis.read(buffer))!=-1) {
                    output.write(buffer, 0, length);
                    length = -1;
                }
                output.flush();
                try {
                    //线程休眠
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketTimeoutException e) {
            if (input != null){
                try {
                    input.close();
                    output.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }catch (IOException e) {

        }finally {
            if (input != null){
                try {
                    input.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
