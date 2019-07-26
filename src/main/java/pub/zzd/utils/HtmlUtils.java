package pub.zzd.utils;

/**
 * @Description :
 * @Author : ZZD
 * @CreateTime : 2019/7/27 1:15
 */
public class HtmlUtils {
    public static final String HTML_404 = "HTTP/1.1 404 Not Found\n" +
            "Content-Type: text/html\n" +
            "\n" +
            "<html>\n" +
            "<head><title>Proxy Connect Exception</title></head>\n" +
            "<body>\n" +
            "<br/>\n" +
            "<center><h1>Proxy Connect Exception</h1></center>\n" +
            "<hr><center>Please refresh the page and try again\n</center>\n" +
            "</body>\n" +
            "</html>";
}
