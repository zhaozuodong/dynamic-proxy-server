package pub.zzd.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
                byte[] bytes = new byte[1024];
                int length=-1;
                while((length=bis.read(bytes))!=-1) {
                    System.out.println(new String(bytes,0,length) );
                    output.write(bytes, 0, length);
                    length = -1;
                }
                output.flush();
                System.out.println("=========================================================================================================");
                break;
            }
        } catch (Exception e) {
            try {
                input.close();
                output.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
