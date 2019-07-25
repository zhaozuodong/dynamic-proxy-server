package pub.zzd.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Description: c3p0的工具类
 * @Author     : ZZD
 * @DateTime   : 2019年07月25日 上午10:14:44
 */
public class MyC3P0Utils {
	//直接创建了一个成员变量,且直接对成员变量初始化
	private static ComboPooledDataSource ds = new ComboPooledDataSource();

	//自定义常用方法
	public static Connection getConnection() throws SQLException{
		return ds.getConnection();
	}
	//自定义一个,获取连接池的方法
	public static DataSource getDataSource(){
		return ds;
	}
	//关闭资源
	public static void close(ResultSet set,Statement st,Connection conn){
		try {
			if(set!=null){
				set.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if(st!=null){
				st.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
