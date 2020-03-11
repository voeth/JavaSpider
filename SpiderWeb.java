package Spider;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.io.InputStreamReader;


public class SpiderWeb{
	InputStreamReader crawl(String url) throws MalformedURLException,IOException{
		URL link = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) link.openConnection();
		conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("accept", "*/*");
		try {
			conn.connect();
			if (conn.getResponseCode()!=200 || conn.getConnectTimeout()!=0) {
				System.out.println(conn.getResponseMessage());
				return null;
			}
		}catch(SocketTimeoutException e) {
			e.printStackTrace();
		}
		return new InputStreamReader(conn.getInputStream());
		
	}
}
