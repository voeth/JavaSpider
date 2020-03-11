package Spider;

public class Poetry {
	String writer;
	String year;
	String content;
	String image;
	Poetry(String year,String writer,String content,String image){
		this.content = content;
		this.year = year;
		this.writer = writer;
		this.image = image;
	}
	
	String getYear() {
		return this.year;
	}
	
	String getContent() {
		return this.content;
	}
	
	String getWriter() {
		return this.writer;
	}
	
	String getImage() {
		return this.image;
	}
}
