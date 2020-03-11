package Spider;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class SpiderTest{
	public static void main(String args[]) throws IOException,InterruptedException{	
		/*
		// 爬取html文件
		int count =1;
		SpiderWeb web = new SpiderWeb();
		while(true) {
			String url = "http://www.shicimingju.com/paiming?p="+count;
			// 判断是否爬取了所有页面
			if(web.crawl(url)!=null) {
				createHtmlFile(web.crawl(url),"E:\\JavaHtml\\"+count+".html");
				System.out.println("爬到了第"+count+"页");
				count +=1;
				// 线程休眠3s，防止过度爬取
				Thread.sleep(3000);
			}
			else {
				System.out.println("页面爬取完毕！");
				break;
			}

		}*/
		
		// 开始批量解析html文件
		File file = new File("E:\\JavaHtml");
		// 解析文件类
		ParseHtml html = new ParseHtml();
		//用HashMap来存解析好的数据
		HashMap data = new HashMap();
		
		try {
			// 一次建立好与数据库的连接，传给其它方法使用
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://0.0.0.0:1433;"+"databaseName=demo;user=chenlang;password=dajiayiqi123;";
			Connection conn = DriverManager.getConnection(url);
			for(String f:file.list()) {
				// 批量解析文件，将数据添加至数据库中
				data =html.parse("E:\\JavaHtml\\"+f);
				insertDB(data,conn);
			}
			conn.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	static void createHtmlFile(InputStreamReader in,String path) throws IOException{
		BufferedReader reader = new BufferedReader(in);
		File file = new File(path);
		file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		String s = "";
		while((s=reader.readLine()) != null) {
			writer.append(s+'\n');
		}
		reader.close();
		writer.close();
	}
	
	static InputStream spiderImage(String u,Connection cursor) {
		try {
			URL url = new URL(u);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			InputStream in =conn.getInputStream();
			return in;
		}catch(Exception e) {e.printStackTrace();}
		return null;
	}
	// throws要求调用方处理的异常
	static void insertDB(HashMap map,Connection conn) throws SQLException,ClassNotFoundException{

		try {
			conn.setAutoCommit(false);
			String sql = "insert into poetryDB(writer,age,title,[content],picture) values(?,?,?,?,?)";
			PreparedStatement cursor = conn.prepareStatement(sql);
			// 将set转为迭代器方便遍历
			Iterator data = map.keySet().iterator();
			while(data.hasNext()) {
				// 获取HashMap的键
				String key = (String) data.next();
				Poetry p =(Poetry) map.get(key);
				cursor.setString(1,key.split("-")[0]);
				cursor.setString(2, p.getYear());
				cursor.setString(3,key.split("-")[1]);
				cursor.setString(4, p.getContent());
				cursor.setString(5, p.getImage());
				cursor.addBatch();
				if (p.getImage()!="") {
					InputStream img = spiderImage(p.getImage(), conn);
					String imgSql = "update poetryDB set imgCode =(?) where content=(?)";
					PreparedStatement pstmt = conn.prepareStatement(imgSql);
					pstmt.setBinaryStream(1, img);
					pstmt.setString(2,p.getContent());
					pstmt.executeLargeUpdate();
					System.out.println("图片储存成功！");
				}
				System.out.println(key+"成功插入表中");
			}
			// 执行sql语句并提交语句
			cursor.executeBatch();
			conn.commit();

		}catch(SQLException e) {
			e.printStackTrace();
			conn.rollback();
		}
	}
}
