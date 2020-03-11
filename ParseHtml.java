package Spider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

// 使用jsoup第三方jar包，解析html文件
public class ParseHtml {

	HashMap parse(String dir) throws IOException{
		File file = new File(dir);
		HashMap <String,Poetry> map = new HashMap();
		Document doc = Jsoup.parse(file, "UTF-8");
		// 获取所有class=card shici_card的节点
		Elements main = doc.getElementsByClass("card shici_card");
		Elements poem = main.select("div.list_num_info");
		Elements content = main.select("div.shici_list_main");
		Elements img = main.select("div.shici_list_pic");
		// 获取标签内的文本
		List <String> poemStr = poem.eachText();
		List <String> contentStr = content.eachText();
		// 专门用来遍历照片链接
		int pic = 0;
		for (int i=0;i<contentStr.size();i++) {

			String str = contentStr.get(i);
			String [] str2 = poemStr.get(i).split(" ");
			str.replace("展开全文", "").replace("收起", "");
			String title = str.substring(str.indexOf("《"), str.indexOf("》")+1);
			String image = "";
			String year;
			String writer;
			String value = str.substring(str.indexOf("》")+1);
			try {
				year = str2[1];
				writer = str2[2];
				
			}catch(ArrayIndexOutOfBoundsException e) {
				year = str2[0];
				writer = str2[1];
			}

			
			// 先获取子节点再判断该节点是否有图片链接（有没有shici_list_pic类）
			/*<div class="card shici_card">
			 * 	<div class="shici_list_pic">
			 * */
			if(main.get(i).children().hasClass("shici_list_pic")) {
				String src = img.get(pic).children().html();
				image = src.substring(src.indexOf("http"), src.indexOf("\">"));
				pic +=1;
			}	
			// 用自定义的Poetry类存储提取出来的数据
			map.put(writer+"-"+title,new Poetry(year,writer,value,image));
		}
		System.out.println(dir+"解析成功");
		return map;
	}
	
}
